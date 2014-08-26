package de.agilecoders.wicket.jquery.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.agilecoders.wicket.jquery.WicketJquerySelectors;
import de.agilecoders.wicket.jquery.settings.ObjectMapperFactory;
import org.apache.wicket.util.io.IClusterable;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.string.Strings;

/**
 * Helper functions to handle JsonNode values.
 *
 * @author miha
 */
public final class Json {

    /**
     * lazy holder to give application the chance to install its own
     */
    private static final class Holder {
        private static final ObjectMapperFactory factory = WicketJquerySelectors.assignedSettingsOrDefault().getObjectMapperFactory();
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private Json() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return a new {@link ObjectMapper} instance which allows single
     *         quotes and unquoted keys by default
     */
    private static ObjectMapper createObjectMapper() {
        return Holder.factory.newObjectMapper();
    }

    /**
     * Convert an object to JsonNode.
     *
     * @param data Value to convert in Json.
     * @return creates a new json object from given data object
     * @throws ParseException to runtime if object can't be parsed
     */
    public static JsonNode toJson(final Object data) {
        if (data == null) {
            return newObject();
        }

        try {
            return createObjectMapper().valueToTree(data);
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    /**
     * Convert a JsonNode to a Java value
     *
     * @param json  Json value to convert.
     * @param clazz Expected Java value type.
     * @param <T>   type of return object
     * @return casted value of given json object
     * @throws ParseException to runtime if json node can't be casted to clazz.
     */
    public static <T> T fromJson(final JsonNode json, final Class<T> clazz) {
        try {
            return createObjectMapper().treeToValue(json, clazz);
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    /**
     * Convert a string to a Java value
     *
     * @param json Json value to convert.
     * @param type Expected Java value type.
     * @param <T>  type of return object
     * @return casted value of given json object
     * @throws ParseException to runtime if json node can't be casted to clazz.
     */
    public static <T> T fromJson(final String json, final JavaType type) {
        try {
            return createObjectMapper().readValue(json, type);
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    /**
     * Convert a JsonNode to a Java value
     *
     * @param json  Json value to convert.
     * @param clazz Expected Java value type.
     * @param <T>   type of return object
     * @return casted value of given json object
     */
    public static <T> T fromJson(final String json, final Class<T> clazz) {
        return fromJson(parse(json), clazz);
    }

    /**
     * Creates a new empty ObjectNode.
     *
     * @return a new empty json object.
     */
    public static ObjectNode newObject() {
        return createObjectMapper().createObjectNode();
    }

    /**
     * Convert a JsonNode to its string representation. If given
     * value is null an empty json object will returned.
     *
     * @param json The json object to toJsonString
     * @return stringified version of given json object
     */
    public static String stringify(final JsonNode json) {
        return json != null ? json.toString() : "{}";
    }

    /**
     * Convert an object to a json string representation. If given
     * value is null an empty json object will returned.
     *
     * @param data The data object to toJsonString
     * @return stringified version of given json object
     */
    public static String stringify(final Object data) {
        return stringify(toJson(data));
    }

    /**
     * verifies a valid json string
     *
     * @param json The json string
     * @return true, if string is a valid json string
     */
    public static boolean isValid(final String json) {
        if (Strings.isEmpty(json)) {
            return false;
        }

        try {
            return parse(json) != null;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Parse a String representing a json, and return it as a JsonNode.
     *
     * @param jsonString string to parse
     * @return parsed json string as json node
     * @throws ParseException to runtime if json string can't be parsed
     */
    public static JsonNode parse(final String jsonString) {
        if (Strings.isEmpty(jsonString)) {
            return newObject();
        }

        try {
            return createObjectMapper().readValue(jsonString, JsonNode.class);
        } catch (Throwable e) {
            throw new ParseException(String.format("can't parse string [%s]", jsonString), e);
        }
    }

    /**
     * Json parser runtime exception.
     */
    public static final class ParseException extends RuntimeException {

        /**
         * Constructor.
         *
         * @param message   The error message
         * @param throwable The cause
         */
        public ParseException(String message, Throwable throwable) {
            super(message, throwable);
        }

        /**
         * Constructor.
         *
         * @param throwable the cause
         */
        public ParseException(Throwable throwable) {
            super(throwable);
        }
    }

    /**
     * An object that can be used to render raw String value,
     * i.e. a String that will not be wrapped in quotes.
     *
     * For example serializing a key 'someKey' with value {@code new RawValue("Value")}
     * will produce the following JSON: {"someKey": Value}.
     *
     * The user must make sure that the value is a real JavaScript object in his application.
     */
    public static final class RawValue implements IClusterable {

        private final String value;

        public RawValue(String value) {
            this.value = Args.notEmpty(value, "value");
        }

        public String value() {
            return value;
        }
    }
}
