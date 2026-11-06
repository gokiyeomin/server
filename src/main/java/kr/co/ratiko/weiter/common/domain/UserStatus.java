package kr.co.ratiko.weiter.common.domain;

/**
 * Whether a user has completed sign-up. A user stays {@code PENDING} until sign-up is complete.
 */
public enum UserStatus {
    PENDING,
    ACTIVE,
}
