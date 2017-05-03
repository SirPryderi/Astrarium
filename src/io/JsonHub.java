package io;

import astrarium.Astrarium;
import astrarium.CelestialBody;
import astrarium.Orbit;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * A static class for loading the save format from the filesystem.
 * <p>
 * Created on 17/02/2017.
 *
 * @author Vittorio
 */
public abstract class JsonHub {

    /**
     * Returns a {@link Gson} object with the default class registered.
     *
     * @return a pre-registered gson object.
     */
    private static Gson getRegisteredGson() {
        return new GsonBuilder()
                .registerTypeAdapter(CelestialBody.class, new CelestialBodySerializer())
                .setPrettyPrinting()
                .create();
    }

    /**
     * Serialises a {@link CelestialBody} to JSON format.
     *
     * @param celestialBody to serialise.
     * @return serialised string.
     */
    public static String exportJson(CelestialBody celestialBody) {
        Gson gson = getRegisteredGson();

        Type type = new TypeToken<CelestialBody>() {
        }.getType();

        return gson.toJson(celestialBody, type);
    }

    /**
     * Serialises an {@link Astrarium} to JSON format.
     *
     * @param astrarium to serialise.
     * @return serialised string.
     */
    public static String exportJson(Astrarium astrarium) {
        Gson gson = getRegisteredGson();

        Type type = new TypeToken<Astrarium>() {
        }.getType();

        return gson.toJson(astrarium, type);
    }

    /**
     * Serialises a {@link CelestialBody} and saves the content to {@code file}.
     *
     * @param file          path to the output file.
     * @param celestialBody to serialise.
     * @throws IOException in case of failure to save the file.
     */
    public static void exportJson(File file, CelestialBody celestialBody) throws IOException {
        String string = exportJson(celestialBody);

        Files.write(file.toPath(), string.getBytes());
    }

    /**
     * Serialises an {@link Astrarium} and saves the content to {@code file}.
     *
     * @param file      path to the output file.
     * @param astrarium to serialise.
     * @throws IOException in case of failure to save the file.
     */
    public static void exportJson(File file, Astrarium astrarium) throws IOException {
        String string = exportJson(astrarium);

        Files.write(file.toPath(), string.getBytes());
    }

    /**
     * Attempts to deserialize a {@code file} into a {@link CelestialBody}.
     *
     * @param file path to the file to deserialize.
     * @return the deserialized object.
     * @throws IOException in case of error when accessing the file.
     */
    public static CelestialBody importCelestialBodyJson(File file) throws IOException {
        String string = readFile(file.getAbsolutePath(), Charset.defaultCharset());

        return importCelestialBodyJson(string);
    }

    /**
     * Attempts to deserialize a {@code string} into a {@link CelestialBody}.
     *
     * @param string to deserialize.
     * @return the deserialized object.
     */
    public static CelestialBody importCelestialBodyJson(String string) {
        Type type = new TypeToken<CelestialBody>() {
        }.getType();

        Gson gson = new GsonBuilder().registerTypeAdapter(CelestialBody.class, new CelestialBodyDeserializer()).create();

        return gson.fromJson(string, type);
    }

    /**
     * Attempts to deserialize a {@code reader} into a {@link CelestialBody}.
     *
     * @param reader to deserialize.
     * @return the deserialized object.
     */
    public static CelestialBody importCelestialBodyJson(Reader reader) {
        Type type = new TypeToken<CelestialBody>() {
        }.getType();

        Gson gson = new GsonBuilder().registerTypeAdapter(CelestialBody.class, new CelestialBodyDeserializer()).create();

        return gson.fromJson(reader, type);
    }

    /**
     * Attempts to deserialize a {@code file} into an {@link Astrarium}.
     *
     * @param file path to the file to deserialize.
     * @return the deserialized object.
     * @throws IOException in case of error when accessing the file.
     */
    public static Astrarium importAstrariumJson(File file) throws IOException {
        String string = readFile(file.getAbsolutePath(), Charset.defaultCharset());

        return importAstrariumJson(string);
    }

    /**
     * Attempts to deserialize a {@code string} into an {@link Astrarium}.
     *
     * @param string to the file to deserialize.
     * @return the deserialized object.
     */
    public static Astrarium importAstrariumJson(String string) {
        Type type = new TypeToken<Astrarium>() {
        }.getType();

        Gson gson = new GsonBuilder().registerTypeAdapter(CelestialBody.class, new CelestialBodyDeserializer()).create();

        return gson.fromJson(string, type);
    }

    /**
     * Attempts to deserialize a {@code reader} into an {@link Astrarium}.
     *
     * @param reader to the file to deserialize.
     * @return the deserialized object.
     */
    public static Astrarium importAstrariumJson(Reader reader) {
        Type type = new TypeToken<Astrarium>() {
        }.getType();

        Gson gson = new GsonBuilder().registerTypeAdapter(CelestialBody.class, new CelestialBodyDeserializer()).create();

        return gson.fromJson(reader, type);
    }

    /**
     * Reads a file and returns a string.
     *
     * @param path     to read.
     * @param encoding the encoding of the file.
     * @return the contents of the file.
     * @throws IOException in case of I/O error.
     */
    private static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    /**
     * Returns one of the default {@link Astrarium}s contained in the data folder.
     *
     * @param solSystem name of the file without the extension.
     * @return the selected {@link Astrarium}.
     * @throws IOException in case of error, like file not found.
     */
    public static Astrarium importDefaultMap(String solSystem) throws IOException {
        InputStream in = JsonHub.class.getResourceAsStream("/astrarium/data/" + solSystem + ".json");
        Reader fr = new InputStreamReader(in, "utf-8");

        return importAstrariumJson(fr);
    }

    //region Deserializers

    /**
     * A class to deserialize a {@link CelestialBody}.
     */
    @SuppressWarnings("JavaDoc")
    private static class CelestialBodyDeserializer implements JsonDeserializer<CelestialBody> {

        @Override
        public CelestialBody deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return deserialize(jsonElement, type, jsonDeserializationContext, null);
        }

        public CelestialBody deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext, CelestialBody parent) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            String name = jsonObject.get("name").getAsString();
            double mass = jsonObject.get("mass").getAsDouble();
            double radius = jsonObject.get("radius").getAsDouble();

            CelestialBody celestialBody;

            if (parent != null) {
                Type orbitType = new TypeToken<Orbit>() {
                }.getType();

                Orbit orbit = new OrbitDeserializer(parent).deserialize(jsonObject.get("orbit"), orbitType, jsonDeserializationContext);

                celestialBody = new CelestialBody(name, mass, radius, orbit);
            } else {
                celestialBody = new CelestialBody(name, mass, radius);
            }


            JsonArray children = jsonObject.get("children").getAsJsonArray();

            for (JsonElement child : children) {
                deserialize(child, type, jsonDeserializationContext, celestialBody);
            }

            return celestialBody;
        }


    }

    /**
     * A class to deserialize an {@link Orbit}.
     */
    @SuppressWarnings("JavaDoc")
    private static class OrbitDeserializer implements JsonDeserializer<Orbit> {
        CelestialBody parent;

        public OrbitDeserializer(CelestialBody parent) {
            this.parent = parent;
        }

        @Override
        public Orbit deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            /*
            object.addProperty("semiMajorAxis", orbit.getSemiMajorAxis());
            object.addProperty("eccentricity", orbit.getEccentricity());
            object.addProperty("inclination", orbit.getInclination());
            object.addProperty("longitudeOfAscendingNode", orbit.getLongitudeOfAscendingNode());
            object.addProperty("argumentOfPeriapsis", orbit.getArgumentOfPeriapsis());
            object.addProperty("meanAnomalyAtEpoch", orb
             */

            JsonObject jsonObject = jsonElement.getAsJsonObject();

            double semiMajorAxis = jsonObject.get("semiMajorAxis").getAsDouble();
            double eccentricity = jsonObject.get("eccentricity").getAsDouble();
            double inclination = jsonObject.get("inclination").getAsDouble();
            double longitudeOfAscendingNode = jsonObject.get("longitudeOfAscendingNode").getAsDouble();
            double argumentOfPeriapsis = jsonObject.get("argumentOfPeriapsis").getAsDouble();
            double meanAnomalyAtEpoch = jsonObject.get("meanAnomalyAtEpoch").getAsDouble();

            return new Orbit(parent, semiMajorAxis, eccentricity, inclination, longitudeOfAscendingNode, argumentOfPeriapsis, meanAnomalyAtEpoch);
        }
    }
    //endregion

    //region Serializers

    /**
     * A class to serialise a {@link CelestialBody}.
     */
    @SuppressWarnings("JavaDoc")
    private static class CelestialBodySerializer implements JsonSerializer<CelestialBody> {

        @Override
        public JsonElement serialize(CelestialBody celestialBody, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject object = new JsonObject();

            JsonArray children = new JsonArray();

            if (celestialBody.getChildren() != null)
                for (CelestialBody child : celestialBody.getChildren()) {
                    children.add(this.serialize(child, type, jsonSerializationContext));
                }

            object.addProperty("name", celestialBody.getName());
            object.addProperty("mass", celestialBody.getMass());
            object.addProperty("radius", celestialBody.getRadius());


            if (celestialBody.getOrbit() != null) {
                Type orbitType = new TypeToken<Orbit>() {
                }.getType();

                object.add("orbit", new OrbitSerializer().serialize(celestialBody.getOrbit(), orbitType, jsonSerializationContext));

            }

            object.add("children", children);

            return object;
        }
    }

    /**
     * A class to serialise an {@link Orbit}.
     */
    private static class OrbitSerializer implements JsonSerializer<Orbit> {
        @Override
        public JsonElement serialize(Orbit orbit, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject object = new JsonObject();
            object.addProperty("semiMajorAxis", orbit.getSemiMajorAxis());
            object.addProperty("eccentricity", orbit.getEccentricity());
            object.addProperty("inclination", orbit.getInclination());
            object.addProperty("longitudeOfAscendingNode", orbit.getLongitudeOfAscendingNode());
            object.addProperty("argumentOfPeriapsis", orbit.getArgumentOfPeriapsis());
            object.addProperty("meanAnomalyAtEpoch", orbit.getMeanAnomalyAtEpoch());
            return object;
        }
    }
    //endregion
}
