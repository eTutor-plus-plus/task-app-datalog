package at.jku.dke.task_app.datalog.services;

import org.sqids.Sqids;

import java.util.Collections;

/**
 * Helper-class to hash ids.
 */
public final class HashIds {
    private HashIds() {
    }

    private static final Sqids SQIDS = Sqids.builder()
        .minLength(4)
        .alphabet("tQWcTyakYope5iu9J6zGfRFrdEvMK7X31ZmxgqsSANU2hjODIV0CwnP4L8lBbH")
        .build();

    /**
     * Encodes the given id.
     *
     * @param id The id to encode.
     * @return The encoded id.
     */
    public static String encode(long id) {
        return SQIDS.encode(Collections.singletonList(id));
    }

    /**
     * Decodes the given id.
     *
     * @param id The id to decode.
     * @return The decoded id.
     */
    public static long decode(String id) {
        return SQIDS.decode(id).getFirst();
    }
}
