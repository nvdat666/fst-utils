package com.example.fstutils;

import java.awt.*;
import java.awt.event.*;

public class KeepScreenOn extends Thread {
    private Long miliSecond;
    private String button;

    public void run() {
        try {
            boolean flag = true;
            do {
                flag = !flag;

                Thread.sleep(miliSecond);

                int codeBtn;
                switch (button) {
                    case "NumLock":
                        codeBtn = KeyEvent.VK_NUM_LOCK;
                        break;
                    case "ScrollLock":
                        codeBtn = KeyEvent.VK_SCROLL_LOCK;
                        break;
                    default:
                        codeBtn = KeyEvent.VK_NUM_LOCK;
                        break;
                }
                Toolkit.getDefaultToolkit().setLockingKeyState(codeBtn, flag);
            } while (true);
        } catch (Exception ignored) {
            interrupt();
        }
    }


    public Long getMiliSecond() {
        return miliSecond;
    }

    public void setMilisSecond(Long miliSecond) {
        this.miliSecond = miliSecond;
    }

    public String getButton() {
        return button;
    }

    public void setButton(String button) {
        this.button = button;
    }
}