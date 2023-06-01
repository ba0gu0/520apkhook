package de.robv.android.xposed;

import android.util.Log;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * This class contains most of Xposed's central logic, such as initialization and callbacks used by
 * the native side. It also includes methods to add new hooks.
 */
@SuppressWarnings("unused")
public final class XposedBridge {
	/**
	 * The system class loader which can be used to locate Android framework classes.
	 * Application classes cannot be retrieved from it.
	 *
	 * @see ClassLoader#getSystemClassLoader
	 */
	public static final ClassLoader BOOTCLASSLOADER = ClassLoader.getSystemClassLoader();

	/** @hide */
	public static final String TAG = "Xposed";

	/** @deprecated Use {@link #getXposedVersion()} instead. */
	@SuppressWarnings("DeprecatedIsStillUsed")
	@Deprecated
	public static int XPOSED_BRIDGE_VERSION;

	private static final Object[] EMPTY_ARRAY = new Object[0];

	// Built-in handlers
	private static final Map<Member, CopyOnWriteSortedSet<XC_MethodHook>> sHookedMethodCallbacks = new HashMap<>();
	/*package*/ static final CopyOnWriteSortedSet<XC_LoadPackage> sLoadedPackageCallbacks = new CopyOnWriteSortedSet<>();
	/*package*/ static final Map<Member, HookInfo> sHookRecords = new HashMap<>();

	static {
		System.loadLibrary("xposed");
	}

	private XposedBridge() { }

	/**
	 * Returns the currently installed version of the Xposed framework.
	 */
	public static int getXposedVersion() {
		return XPOSED_BRIDGE_VERSION;
	}

	/**
	 * Writes a message to the Xposed error log.
	 *
	 * <p class="warning"><b>DON'T FLOOD THE LOG!!!</b> This is only meant for error logging.
	 * If you want to write information/debug messages, use logcat.
	 *
	 * @param text The log message.
	 */
	public static synchronized void log(String text) {
		Log.i(TAG, text);
	}

	/**
	 * Logs a stack trace to the Xposed error log.
	 *
	 * <p class="warning"><b>DON'T FLOOD THE LOG!!!</b> This is only meant for error logging.
	 * If you want to write information/debug messages, use logcat.
	 *
	 * @param t The Throwable object for the stack trace.
	 */
	public static synchronized void log(Throwable t) {
		Log.e(TAG, Log.getStackTraceString(t));
	}

	/**
	 * Hook any method (or constructor) with the specified callback. See below for some wrappers
	 * that make it easier to find a method/constructor in one step.
	 *
	 * @param hookMethod The method to be hooked.
	 * @param callback The callback to be executed when the hooked method is called.
	 * @return An object that can be used to remove the hook.
	 *
	 * @see XposedHelpers#findAndHookMethod(String, ClassLoader, String, Object...)
	 * @see XposedHelpers#findAndHookMethod(Class, String, Object...)
	 * @see #hookAllMethods
	 * @see XposedHelpers#findAndHookConstructor(String, ClassLoader, Object...)
	 * @see XposedHelpers#findAndHookConstructor(Class, Object...)
	 * @see #hookAllConstructors
	 */
	public static XC_MethodHook.Unhook hookMethod(Member hookMethod, XC_MethodHook callback) {
		if (!(hookMethod instanceof Method) && !(hookMethod instanceof Constructor<?>)) {
			throw new IllegalArgumentException("Only methods and constructors can be hooked: " + hookMethod.toString());
		} else if (Modifier.isAbstract(hookMethod.getModifiers())) {
			throw new IllegalArgumentException("Cannot hook abstract methods: " + hookMethod);
		}

		CopyOnWriteSortedSet<XC_MethodHook> callbacks;
		synchronized (sHookedMethodCallbacks) {
			callbacks = sHookedMethodCallbacks.get(hookMethod);
			if (callbacks == null) {
				callbacks = new CopyOnWriteSortedSet<>();
				HookInfo hookInfo = new HookInfo(callbacks);

				try {
					Method backup = hook0(hookInfo, hookMethod, hookInfo.getClass().getDeclaredMethod("callback", Object[].class));
					if (backup == null) {
						return null;
					}

					hookInfo.backup = backup;
				} catch (NoSuchMethodException e) {
					Log.e(TAG, e.getMessage());
				}

				sHookedMethodCallbacks.put(hookMethod, callbacks);
				sHookRecords.put(hookMethod, hookInfo);
			}
		}

		callbacks.add(callback);
		return callback.new Unhook(hookMethod);
	}

	/**
	 * Removes the callback for a hooked method/constructor.
	 *
	 * @deprecated Use {@link XC_MethodHook.Unhook#unhook} instead. An instance of the {@code Unhook}
	 * class is returned when you hook the method.
	 *
	 * @param hookMethod The method for which the callback should be removed.
	 * @param callback The reference to the callback as specified in {@link #hookMethod}.
	 */
	@SuppressWarnings("DeprecatedIsStillUsed")
	@Deprecated
	public static void unhookMethod(Member hookMethod, XC_MethodHook callback) {
		CopyOnWriteSortedSet<XC_MethodHook> callbacks;
		synchronized (sHookedMethodCallbacks) {
			callbacks = sHookedMethodCallbacks.get(hookMethod);
			if (callbacks == null) {
				return;
			}
		}
		callbacks.remove(callback);
	}

	/**
	 * Hooks all methods with a certain name that were declared in the specified class. Inherited
	 * methods and constructors are not considered. For constructors, use
	 * {@link #hookAllConstructors} instead.
	 *
	 * @param hookClass The class to check for declared methods.
	 * @param methodName The name of the method(s) to hook.
	 * @param callback The callback to be executed when the hooked methods are called.
	 * @return A set containing one object for each found method which can be used to unhook it.
	 */
	@SuppressWarnings("UnusedReturnValue")
	public static Set<XC_MethodHook.Unhook> hookAllMethods(Class<?> hookClass, String methodName, XC_MethodHook callback) {
		Set<XC_MethodHook.Unhook> unhooks = new HashSet<>();
		for (Member method : hookClass.getDeclaredMethods()) {
			if (method.getName().equals(methodName)) {
				unhooks.add(hookMethod(method, callback));
			}
		}
		return unhooks;
	}

	/**
	 * Hook all constructors of the specified class.
	 *
	 * @param hookClass The class to check for constructors.
	 * @param callback The callback to be executed when the hooked constructors are called.
	 * @return A set containing one object for each found constructor which can be used to unhook it.
	 */
	@SuppressWarnings("UnusedReturnValue")
	public static Set<XC_MethodHook.Unhook> hookAllConstructors(Class<?> hookClass, XC_MethodHook callback) {
		Set<XC_MethodHook.Unhook> unhooks = new HashSet<>();
		for (Member constructor : hookClass.getDeclaredConstructors()) {
			unhooks.add(hookMethod(constructor, callback));
		}
		return unhooks;
	}

	/**
	 * Adds a callback to be executed when an app ("Android package") is loaded.
	 *
	 * <p class="note">You probably don't need to call this. Simply implement {@link IXposedHookLoadPackage}
	 * in your module class and Xposed will take care of registering it as a callback.
	 *
	 * @param callback The callback to be executed.
	 * @hide
	 */
	public static void hookLoadPackage(XC_LoadPackage callback) {
		synchronized (sLoadedPackageCallbacks) {
			sLoadedPackageCallbacks.add(callback);
		}
	}

	private static native synchronized Method hook0(Object contextObject, Member originalMember, Method callbackMethod);

	/**
	 * Basically the same as {@link Method#invoke}, but calls the original method
	 * as it was before the interception by Xposed. Also, access permissions are not checked.
	 * If the given method is not hooked, the behavior is undefined, Pine does not guarantee this
	 * will always work and may crash on other Xposed framework implementations.
	 *
	 * <p class="caution">There are very few cases where this method is needed. A common mistake is
	 * to replace a method and then invoke the original one based on dynamic conditions. This
	 * creates overhead and skips further hooks by other modules. Instead, just hook (don't replace)
	 * the method and call {@code param.setResult(null)} in {@link XC_MethodHook#beforeHookedMethod}
	 * if the original method should be skipped.
	 *
	 * @param method The method to be called.
	 * @param thisObject For non-static calls, the "this" pointer, otherwise {@code null}.
	 * @param args Arguments for the method call as Object[] array.
	 * @return The result returned from the invoked method.
	 * @throws NullPointerException
	 *             if {@code receiver == null} for a non-static method
	 * @throws IllegalAccessException
	 *             if this method is not accessible (see {@link AccessibleObject})
	 * @throws IllegalArgumentException
	 *             if the number of arguments doesn't match the number of parameters, the receiver
	 *             is incompatible with the declaring class, or an argument could not be unboxed
	 *             or converted by a widening conversion to the corresponding parameter type
	 * @throws InvocationTargetException
	 *             if an exception was thrown by the invoked method
	 */
	public static Object invokeOriginalMethod(Member method, Object thisObject, Object[] args)
			throws NullPointerException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (args == null) {
			args = EMPTY_ARRAY;
		}

		HookInfo hookInfo = sHookRecords.get(method);
		try {
			if (hookInfo != null) {
				return invokeMethod(hookInfo.backup, thisObject, args);
			}

			if (method == null) {
				throw new NullPointerException("Method must not be null");
			}

			if (!(method instanceof Method || method instanceof Constructor<?>)) {
				throw new IllegalArgumentException("Method must be a Method or Constructor");
			}

			int modifiers = method.getModifiers();
			if (Modifier.isAbstract(modifiers)) {
				throw new IllegalArgumentException("Method must not be abstract");
			}
			return invokeMethod(method, thisObject, args);
		} catch (InstantiationException ex) {
			Log.e(TAG, ex.getMessage());
		}
		return null;
	}

	private static Object invokeMethod(Member member, Object thisObject, Object[] args)
			throws IllegalAccessException, InvocationTargetException, InstantiationException {
		if (member instanceof Method) {
			Method method = (Method) member;
			method.setAccessible(true);
			return method.invoke(thisObject, args);
		} else {
			Constructor<?> constructor = (Constructor<?>) member;
			constructor.setAccessible(true);
			return constructor.newInstance(args);
		}
	}

	/** @hide */
	public static final class HookInfo {
		boolean isStatic;
		final CopyOnWriteSortedSet<XC_MethodHook> callbacks;
		Class<?> returnType;
		Member backup;
		Member method;

		HookInfo(CopyOnWriteSortedSet<XC_MethodHook> callbacks) {
			this.callbacks = callbacks;
		}

		public Object callback(Object[] args) throws Throwable {
			XC_MethodHook.MethodHookParam param = new XC_MethodHook.MethodHookParam();
			param.method = method;

			if (isStatic) {
				param.thisObject = null;
				param.args = args;
			} else {
				param.thisObject = args[0];
				param.args = new Object[args.length - 1];
				System.arraycopy(args, 1, param.args, 0, args.length - 1);
			}

			Object[] hooks = callbacks.getSnapshot();
			int hookCount = hooks.length;

			if (hookCount == 0) {
				try {
					return invokeMethod(backup, param.thisObject, param.args);
				} catch (InvocationTargetException e) {
					Log.e(TAG, e.getMessage());
				}
			}

			int beforeIdx = 0;
			do {
				try {
					((XC_MethodHook) hooks[beforeIdx]).beforeHookedMethod(param);
				} catch (Throwable t) {
					Log.e(TAG, t.getMessage());

					param.setResult(null);
					param.returnEarly = false;
					continue;
				}

				if (param.returnEarly) {
					beforeIdx++;
					break;
				}
			} while (++beforeIdx < hookCount);

			if (!param.returnEarly) {
				try {
					param.setResult(invokeMethod(backup, param.thisObject, param.args));
				} catch (InvocationTargetException e) {
					param.setThrowable(e.getCause());
				}
			}

			int afterIdx = beforeIdx - 1;
			do {
				Object lastResult = param.getResult();
				Throwable lastThrowable = param.getThrowable();

				try {
					((XC_MethodHook) hooks[afterIdx]).afterHookedMethod(param);
				} catch (Throwable t) {
					Log.e(TAG, t.getMessage());

					if (lastThrowable == null) {
						param.setResult(lastResult);
					} else {
						param.setThrowable(lastThrowable);
					}
				}
			} while (--afterIdx >= 0);

			Object result = param.getResultOrThrowable();
			if (returnType != null) {
				result = returnType.cast(result);
			}
			return result;
		}
	}

	/** @hide */
	public static final class CopyOnWriteSortedSet<E> {
		private transient volatile Object[] elements = new Object[0];

		@SuppressWarnings("UnusedReturnValue")
		public synchronized boolean add(E e) {
			int index = indexOf(e);
			if (index >= 0) {
				return false;
			}

			Object[] newElements = new Object[elements.length + 1];
			System.arraycopy(elements, 0, newElements, 0, elements.length);

			newElements[elements.length] = e;
			Arrays.sort(newElements);

			elements = newElements;
			return true;
		}

		@SuppressWarnings("UnusedReturnValue")
		public synchronized boolean remove(E e) {
			int index = indexOf(e);
			if (index == -1) {
				return false;
			}

			Object[] newElements = new Object[elements.length - 1];
			System.arraycopy(elements, 0, newElements, 0, index);
			System.arraycopy(elements, index + 1, newElements, index, elements.length - index - 1);

			elements = newElements;
			return true;
		}

		private int indexOf(Object o) {
			for (int i = 0; i < elements.length; i++) {
				if (o.equals(elements[i])) {
					return i;
				}
			}
			return -1;
		}

		public Object[] getSnapshot() {
			return elements;
		}
	}
}
