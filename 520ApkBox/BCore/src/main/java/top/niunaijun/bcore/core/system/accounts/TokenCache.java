package top.niunaijun.bcore.core.system.accounts;

import android.accounts.Account;

import java.util.Objects;

public class TokenCache {
    public final int userId;
    public final Account account;
    public final long expiryEpochMillis;
    public final String authToken;
    public final String authTokenType;
    public final String packageName;

    public TokenCache(int userId, Account account, String callerPkg, String tokenType, String token, long expiryMillis) {
        this.userId = userId;
        this.account = account;
        this.expiryEpochMillis = expiryMillis;
        this.authToken = token;
        this.authTokenType = tokenType;
        this.packageName = callerPkg;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof TokenCache)) {
            return false;
        }
        TokenCache that = (TokenCache) o;
        return userId == that.userId && expiryEpochMillis == that.expiryEpochMillis && Objects.equals(account, that.account) &&
                Objects.equals(authToken, that.authToken) && Objects.equals(authTokenType, that.authTokenType) && Objects.equals(packageName, that.packageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, account, expiryEpochMillis, authToken, authTokenType, packageName);
    }
}
