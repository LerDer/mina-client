package com.lww.mina.session;

import org.apache.mina.core.session.IoSession;

/**
 * @author lww
 */
public class SessionManager {

    private static IoSession SERVER_SESSION;

    public synchronized static void setSession(IoSession session) {
        if (SERVER_SESSION == null) {
            SERVER_SESSION = session;
        }
    }

    public static IoSession getSession() {
        return SERVER_SESSION;
    }
}
