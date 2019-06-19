package com.example.mgesture;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.Serializable;

import android.gesture.GesturePoint;

public class MGesturePoint  implements Serializable {
    public float x;
    public float y;
    public final long timestamp;
public MGesturePoint(float x, float y, long t) {
		this.x=x;
		this.y=y;
		this.timestamp=t;
	}
static GesturePoint deserialize(DataInputStream in) throws IOException {
    // Read X and Y
    final float x = in.readFloat();
    final float y = in.readFloat();
    // Read timestamp
    final long timeStamp = in.readLong();
    return new GesturePoint(x, y, timeStamp);
}
    @Override
    public Object clone() {
        return new GesturePoint(x, y, timestamp);
    }	
}
