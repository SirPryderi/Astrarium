package io;

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

    public static String exportJson(CelestialBody celestialBody) {
        Gson gson = new GsonBuilder().registerTypeAdapter(CelestialBody.class, new CelestialBodySerializer()).setPrettyPrinting().create();

        Type type = new TypeToken<CelestialBody>() {
        }.getType();

        return gson.toJson(celestialBody, type);
    }

    public static CelestialBody importJson(File file) throws IOException {
        String string = readFile(file.getAbsolutePath(), Charset.defaultCharset());

        return importJson(string);
    }

    public static CelestialBody importJson(String string) {
        Type type = new TypeToken<CelestialBody>() {
        }.getType();

        Gson gson = new GsonBuilder().registerTypeAdapter(CelestialBody.class, new CelestialBodyDeserializer()).create();

        return gson.fromJson(string, type);
    }

    public static CelestialBody importJson(Reader reader) {
        Type type = new TypeToken<CelestialBody>() {
        }.getType();

        Gson gson = new GsonBuilder().registerTypeAdapter(CelestialBody.class, new CelestialBodyDeserializer()).create();

        return gson.fromJson(reader, type);
    }

    private static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static CelestialBody importDefaultMap(String solSystem) throws IOException {
        InputStream in = JsonHub.class.getResourceAsStream("/astrarium/data/" + solSystem + ".json");
        Reader fr = new InputStreamReader(in, "utf-8");

        return importJson(fr);
    }

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
}
