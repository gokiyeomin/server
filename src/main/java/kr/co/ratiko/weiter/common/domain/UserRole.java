package kr.co.ratiko.weiter.common.domain;

/**
 * What a user may do. This is a different axis from {@link UserStatus}: the status says whether the user can use
 * the service, and the role says how far the user can change things.
 */
public enum UserRole {
    NORMAL,
    ADMIN,
}
