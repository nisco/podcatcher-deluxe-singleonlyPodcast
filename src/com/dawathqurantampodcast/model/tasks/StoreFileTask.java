

package com.dawathqurantampodcast.model.tasks;

import android.os.AsyncTask;

import com.dawathqurantampodcast.model.types.Progress;


import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Abstract task for file writing.
 * 
 * @param <Params> Params as defined by {@link AsyncTask}
 */
public abstract class StoreFileTask<Params> extends AsyncTask<Params, Progress, Void> {

    /** The file encoding */
    public static final String FILE_ENCODING = "utf8";
    /** The indent char */
    protected static final char INDENT = ' ';

    /** The file writer */
    protected BufferedWriter writer;

    /**
     * @param level Indent level to put in front of line.
     * @param line Actual text to write.
     * @throws IOException If writing the line goes wrong.
     */
    protected void writeLine(int level, String line) throws IOException {
        for (int i = 0; i < level * 2; i++)
            writer.write(INDENT);

        writer.write(line);
        writer.newLine();
    }
}
