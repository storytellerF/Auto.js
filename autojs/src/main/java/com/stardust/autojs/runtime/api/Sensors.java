package com.stardust.autojs.runtime.api;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stardust.autojs.core.eventloop.EventEmitter;
import com.stardust.autojs.core.looper.Loopers;
import com.stardust.autojs.runtime.ScriptBridges;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.util.MapBuilder;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Stardust on 2018/2/5.
 */

public class Sensors extends EventEmitter implements Loopers.LooperQuitHandler {


    private static final Map<String, Integer> SENSORS = new MapBuilder<String, Integer>()
            .put("ACCELEROMETER", Sensor.TYPE_ACCELEROMETER)
            .put("MAGNETIC_FIELD", Sensor.TYPE_MAGNETIC_FIELD)
            .put("ORIENTATION", Sensor.TYPE_ORIENTATION)
            .put("GYROSCOPE", Sensor.TYPE_GYROSCOPE)
            .put("LIGHT", Sensor.TYPE_LIGHT)
            .put("TEMPERATURE", Sensor.TYPE_TEMPERATURE)
            .put("PRESSURE", Sensor.TYPE_PRESSURE)
            .put("AMBIENT_TEMPERATURE", Sensor.TYPE_AMBIENT_TEMPERATURE)
            .put("PROXIMITY", Sensor.TYPE_PROXIMITY)
            .put("GRAVITY", Sensor.TYPE_GRAVITY)
            .put("LINEAR_ACCELERATION", Sensor.TYPE_LINEAR_ACCELERATION)
            .put("RELATIVE_HUMIDITY", Sensor.TYPE_RELATIVE_HUMIDITY)
            .put("AMBIENT_TEMPERATURE", Sensor.TYPE_AMBIENT_TEMPERATURE)
            .build();
    public final Delay delay = new Delay();
    private final Set<SensorEventEmitter> mSensorEventEmitters = new HashSet<>();
    @NonNull
    private final SensorManager mSensorManager;
    private final ScriptBridges mScriptBridges;
    @NonNull
    private final SensorEventEmitter mNoOpSensorEventEmitter;
    @NonNull
    private final ScriptRuntime mScriptRuntime;
    public boolean ignoresUnsupportedSensor = false;
    public Sensors(@NonNull Context context, @NonNull ScriptRuntime runtime) {
        super(runtime.bridges);
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mScriptBridges = runtime.bridges;
        mNoOpSensorEventEmitter = new SensorEventEmitter(runtime.bridges);
        mScriptRuntime = runtime;
        runtime.loopers.addLooperQuitHandler(this);
    }

    @Nullable
    public SensorEventEmitter register(String sensorName) {
        return register(sensorName, Delay.normal);
    }

    @Nullable
    public SensorEventEmitter register(@Nullable String sensorName, int delay) {
        if (sensorName == null)
            throw new NullPointerException("sensorName = null");
        Sensor sensor = getSensor(sensorName);
        if (sensor == null) {
            if (ignoresUnsupportedSensor) {
                emit("unsupported_sensor", sensorName);
                return mNoOpSensorEventEmitter;
            } else {
                return null;
            }
        }
        return register(sensor, delay);
    }

    @NonNull
    private SensorEventEmitter register(@NonNull Sensor sensor, int delay) {
        SensorEventEmitter emitter = new SensorEventEmitter(mScriptBridges);
        mSensorManager.registerListener(emitter, sensor, delay);
        synchronized (mSensorEventEmitters) {
            mSensorEventEmitters.add(emitter);
        }
        return emitter;
    }

    @Override
    public boolean shouldQuit() {
        return mSensorEventEmitters.isEmpty();
    }

    @Nullable
    public Sensor getSensor(@NonNull String sensorName) {
        Integer type = SENSORS.get(sensorName.toUpperCase());
        if (type == null)
            type = getSensorTypeByReflect(sensorName);
        if (type == null)
            return null;
        return mSensorManager.getDefaultSensor(type);
    }

    @Nullable
    private Integer getSensorTypeByReflect(String sensorName) {
        sensorName = sensorName.toUpperCase();
        try {
            Field field = Sensor.class.getField("TYPE_" + sensorName);
            return (Integer) field.get(null);
        } catch (Exception e) {
            return null;
        }
    }

    public void unregister(@Nullable SensorEventEmitter emitter) {
        if (emitter == null)
            return;
        synchronized (mSensorEventEmitters) {
            mSensorEventEmitters.remove(emitter);
        }
        mSensorManager.unregisterListener(emitter);
    }

    public void unregisterAll() {
        synchronized (mSensorEventEmitters) {
            for (SensorEventEmitter emitter : mSensorEventEmitters) {
                mSensorManager.unregisterListener(emitter);
            }
            mSensorEventEmitters.clear();
        }
        mScriptRuntime.loopers.removeLooperQuitHandler(this);
    }

    public static class Delay {
        public static final int normal = SensorManager.SENSOR_DELAY_NORMAL;
        public static final int ui = SensorManager.SENSOR_DELAY_UI;
        public static final int game = SensorManager.SENSOR_DELAY_GAME;
        public static final int fastest = SensorManager.SENSOR_DELAY_FASTEST;
    }

    public class SensorEventEmitter extends EventEmitter implements SensorEventListener {

        public SensorEventEmitter(ScriptBridges bridges) {
            super(bridges);
        }

        @Override
        public void onSensorChanged(@NonNull SensorEvent event) {
            Object[] args = new Object[event.values.length + 1];
            args[0] = event;
            for (int i = 1; i < args.length; i++) {
                args[i] = event.values[i - 1];
            }
            emit("change", args);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            emit("accuracy_change", accuracy);
        }

        public void unregister() {
            Sensors.this.unregister(this);
        }
    }
}
