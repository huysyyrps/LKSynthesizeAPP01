package com.example.lksynthesizeapp.ChiFen.test;

import android.text.TextUtils;

public class ModbusError extends Exception {
    private int code;

    public ModbusError(int code, String message) {
        super(!TextUtils.isEmpty(message) ? message : "Modbus Error: Exception code = " + code);
        this.code = code;
    }

    public ModbusError(int code) {
        this(code, null);
    }

    public ModbusError(ModbusErrorType type, String message) {
        super(type.name() + ": " + message);
    }

    public ModbusError(String message) {
        super(message);
    }

    public int getCode() {
        return this.code;
    }
}

