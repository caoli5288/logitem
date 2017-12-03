/*
 * Decompiled with CFR 0_123.
 */
package com.i5mc.logitem;

import com.i5mc.logitem.Main;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class MainLogger
extends Logger {
    public MainLogger() {
        super("logitem", null);
        try {
            FileHandler handler = new FileHandler("logitem.log", true);
            handler.setFormatter(new LogFormatter());
            this.setLevel(Level.ALL);
            this.addHandler(handler);
        }
        catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    static class LogFormatter
    extends Formatter {
        final DateFormat format = new SimpleDateFormat("MM-dd HH:mm:ss");

        LogFormatter() {
        }

        @Override
        public String format(LogRecord record) {
            StringBuilder i = new StringBuilder();
            i.append("[");
            i.append(this.format.format(record.getMillis()));
            i.append(" ");
            i.append(record.getLevel().getName());
            i.append("] ");
            i.append(this.formatMessage(record));
            i.append('\n');
            Throwable thrown = record.getThrown();
            if (!(thrown == null)) {
                StringWriter writer = new StringWriter();
                thrown.printStackTrace(new PrintWriter(writer));
                i.append(writer);
            }
            return i.toString();
        }
    }

}

