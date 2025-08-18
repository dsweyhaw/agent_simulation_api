package com.uet.agent_simulation_api.constant.user;

public final class Role {
    public static final int ADMIN = 1;
    public static final int USER = 2;

    public static String getRole(int role) {
        return switch (role) {
            case ADMIN -> "1";
            default -> "2";
        };
    }
}
