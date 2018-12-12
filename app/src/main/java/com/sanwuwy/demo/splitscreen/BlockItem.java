package com.sanwuwy.demo.splitscreen;

public class BlockItem {

    private int position;
    private int state;

    public BlockItem(int position, int state) {
        this.position = position;
        this.state = state;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
