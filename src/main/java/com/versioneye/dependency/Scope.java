package com.versioneye.dependency;

public enum Scope {
    SYSTEM(0, "system"),
    COMPILE(1, "compile"),
    PROVIDED(2, "provided"),
    RUNTIME(3, "runtime"),
    TEST(4, "test"),
    ;

    private final int value;
    private final String name;

    Scope(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Scope scopeFromString(String name) {
        for(Scope scope : values()) {
            if(scope.getName().equals(name)) {
                return scope;
            }
        }
        throw new IllegalArgumentException("Unknown scope: " + name);
    }

    public static Scope getStrongestScope(Scope scope1, Scope scope2) {
        return scope1.value < scope2.value ? scope1 : scope2;
    }
}
