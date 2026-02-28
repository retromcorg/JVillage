package com.johnymuffin.jvillage.beta.integrations;

import com.johnymuffin.jvillage.beta.JVillage;
import com.johnymuffin.jvillage.beta.models.Village;
import com.johnymuffin.jvillage.beta.models.chunk.VClaim;
import org.bukkit.Bukkit;

import java.lang.reflect.*;
import java.util.*;
import java.util.logging.Level;

/**
 * Integration with BlueMap to display village claims on the map using reflection
 * This allows compilation with Java 8 while using BlueMap API at runtime
 */
public class BlueMapIntegration {
    
    private final JVillage plugin;
    private boolean enabled = false;
    private Map<String, Integer> villageColors = new HashMap<>();
    private Random colorRandom = new Random();
    private static final String MARKER_SET_ID = "jvillage";
    private int updateTaskId = -1;
    
    // Reflected classes and methods
    private Class<?> blueMapAPIClass;
    private Class<?> blueMapMapClass;
    private Class<?> markerSetClass;
    private Class<?> shapeMarkerClass;
    private Class<?> colorClass;
    private Class<?> shapeClass;
    private Method getInstanceMethod;
    private Method getMapsMethod;
    private Method getMarkerSetsMethod;
    private Method onEnableMethod;
    
    public BlueMapIntegration(JVillage plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Initialize BlueMap integration
     * @return true if successfully initialized
     */
    public boolean initialize() {
        // Check if BlueMap integration is enabled in config
        if (!plugin.getSettings().getConfigBoolean("settings.bluemap.enabled")) {
            plugin.logger(Level.INFO, "BlueMap integration is disabled in config");
            return false;
        }
        
        // Check if BlueMap is installed
        if (Bukkit.getPluginManager().getPlugin("BlueMap") == null) {
            plugin.logger(Level.INFO, "BlueMap not found, map integration disabled");
            return false;
        }
        
        // Try to load BlueMap API classes via reflection
        try {
            blueMapAPIClass = Class.forName("de.bluecolored.bluemap.api.BlueMapAPI");
            blueMapMapClass = Class.forName("de.bluecolored.bluemap.api.BlueMapMap");
            markerSetClass = Class.forName("de.bluecolored.bluemap.api.markers.MarkerSet");
            shapeMarkerClass = Class.forName("de.bluecolored.bluemap.api.markers.ShapeMarker");
            colorClass = Class.forName("de.bluecolored.bluemap.api.math.Color");
            shapeClass = Class.forName("de.bluecolored.bluemap.api.math.Shape");
            
            getInstanceMethod = blueMapAPIClass.getMethod("getInstance");
            getMapsMethod = blueMapAPIClass.getMethod("getMaps");
            
            // Load Consumer class
            Class<?> consumerClass = Class.forName("java.util.function.Consumer");
            onEnableMethod = blueMapAPIClass.getMethod("onEnable", consumerClass);
            getMarkerSetsMethod = blueMapMapClass.getMethod("getMarkerSets");
            
            plugin.logger(Level.INFO, "BlueMap API classes loaded successfully via reflection");
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            plugin.logger(Level.WARNING, "BlueMap API not available: " + e.getMessage());
            return false;
        }
        
        // Register onEnable callback
        try {
            InvocationHandler handler = new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (method.getName().equals("accept")) {
                        enabled = true;
                        plugin.logger(Level.INFO, "BlueMap API enabled, updating markers");
                        updateAllMarkers();
                        startAutoUpdate();
                        return null;
                    } else if (method.getName().equals("hashCode")) {
                        return System.identityHashCode(proxy);
                    } else if (method.getName().equals("equals")) {
                        return proxy == args[0];
                    } else if (method.getName().equals("toString")) {
                        return "BlueMapConsumer@" + Integer.toHexString(System.identityHashCode(proxy));
                    }
                    return null;
                }
            };
            
            Class<?> consumerClass = Class.forName("java.util.function.Consumer");
            Object consumer = Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[] { consumerClass },
                handler
            );
            
            onEnableMethod.invoke(null, consumer);
            plugin.logger(Level.INFO, "BlueMap integration initialized");
            return true;
        } catch (Exception e) {
            plugin.logger(Level.SEVERE, "Failed to initialize BlueMap integration: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update all village markers on all maps
     */
    public void updateAllMarkers() {
        if (!enabled) return;
        
        try {
            int totalVillages = plugin.getVillageMap().getKnownVillages().length;
            plugin.logger(Level.INFO, "Updating BlueMap markers for " + totalVillages + " villages");
            
            // Get BlueMap API instance
            Object optionalApi = getInstanceMethod.invoke(null);
            Method isPresentMethod = optionalApi.getClass().getMethod("isPresent");
            if (!(Boolean) isPresentMethod.invoke(optionalApi)) {
                plugin.logger(Level.WARNING, "BlueMap API not available");
                return;
            }
            
            Method getMethod = optionalApi.getClass().getMethod("get");
            Object api = getMethod.invoke(optionalApi);
            
            // Get all maps
            Collection<?> maps = (Collection<?>) getMapsMethod.invoke(api);
            
            // Update each map
            for (Object map : maps) {
                updateMarkersForMap(map);
            }
            
            plugin.logger(Level.INFO, "Updated BlueMap markers for all villages");
        } catch (Exception e) {
            plugin.logger(Level.SEVERE, "Error updating BlueMap markers: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Update markers for a specific world
     */
    public void updateMarkersForWorld(String worldName) {
        if (!enabled) return;
        
        try {
            // Get BlueMap API instance
            Object optionalApi = getInstanceMethod.invoke(null);
            Method isPresentMethod = optionalApi.getClass().getMethod("isPresent");
            if (!(Boolean) isPresentMethod.invoke(optionalApi)) {
                return;
            }
            
            Method getMethod = optionalApi.getClass().getMethod("get");
            Object api = getMethod.invoke(optionalApi);
            
            // Get all maps
            Collection<?> maps = (Collection<?>) getMapsMethod.invoke(api);
            
            // Update maps for this world
            for (Object map : maps) {
                Method getWorldMethod = map.getClass().getMethod("getWorld");
                Object world = getWorldMethod.invoke(map);
                Method getIdMethod = world.getClass().getMethod("getId");
                String mapWorldName = (String) getIdMethod.invoke(world);
                
                if (mapWorldName.equals(worldName)) {
                    updateMarkersForMap(map);
                }
            }
        } catch (Exception e) {
            plugin.logger(Level.WARNING, "Error updating markers for world " + worldName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Update markers for a specific map
     */
    private void updateMarkersForMap(Object map) {
        try {
            // Get map ID (this is the name like "world" or "world_nether")
            Method getMapIdMethod = map.getClass().getMethod("getId");
            String mapId = (String) getMapIdMethod.invoke(map);
            
            // Get marker sets map
            Map<String, Object> markerSets = (Map<String, Object>) getMarkerSetsMethod.invoke(map);
            
            // Get or create marker set
            Object markerSet = markerSets.get(MARKER_SET_ID);
            if (markerSet == null) {
                String markerLabel = plugin.getSettings().getConfigString("settings.bluemap.marker-label");
                if (markerLabel == null || markerLabel.isEmpty()) {
                    markerLabel = "Village Claims";
                }
                
                Constructor<?> markerSetConstructor = markerSetClass.getConstructor(String.class, boolean.class, boolean.class);
                markerSet = markerSetConstructor.newInstance(markerLabel, true, false);
                markerSets.put(MARKER_SET_ID, markerSet);
            }
            
            // Get markers map and clear it
            Method getMarkersMethod = markerSet.getClass().getMethod("getMarkers");
            Map<String, Object> markers = (Map<String, Object>) getMarkersMethod.invoke(markerSet);
            markers.clear();
            
            int markerCount = 0;
            
            // Add markers for each village claim
            for (UUID villageUUID : plugin.getVillageMap().getKnownVillages()) {
                Village village = plugin.getVillageMap().getVillage(villageUUID);
                if (village == null) continue;
                
                // Get claims for this world
                ArrayList<VClaim> allClaims = village.getClaims();
                ArrayList<VClaim> claims = new ArrayList<>();
                for (VClaim claim : allClaims) {
                    if (claim.getWorldName().equals(mapId)) {
                        claims.add(claim);
                    }
                }
                if (claims.isEmpty()) continue;
                
                // Get or generate color for this village
                int colorInt = getVillageColor(villageUUID);
                Object color = createColor(colorInt);
                
                // Group chunks into contiguous regions and create merged markers
                List<List<VClaim>> regions = groupContiguousClaims(claims);
                
                int regionIndex = 0;
                for (List<VClaim> region : regions) {
                    String markerId = village.getTownUUID().toString() + "_region_" + regionIndex;
                    Object marker = createRegionMarker(village, region, color);
                    markers.put(markerId, marker);
                    markerCount++;
                    regionIndex++;
                }
            }
            
            plugin.logger(Level.INFO, "Updated " + markerCount + " markers for map: " + mapId);
            
        } catch (Exception e) {
            plugin.logger(Level.WARNING, "Error updating markers for map: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Group claims into contiguous regions (connected chunks)
     */
    private List<List<VClaim>> groupContiguousClaims(ArrayList<VClaim> claims) {
        List<List<VClaim>> regions = new ArrayList<>();
        Set<VClaim> processed = new HashSet<>();
        
        for (VClaim claim : claims) {
            if (processed.contains(claim)) continue;
            
            // Start a new region with this claim
            List<VClaim> region = new ArrayList<>();
            Queue<VClaim> queue = new LinkedList<>();
            queue.add(claim);
            processed.add(claim);
            
            // Flood fill to find all connected chunks
            while (!queue.isEmpty()) {
                VClaim current = queue.poll();
                region.add(current);
                
                // Check all 4 adjacent chunks
                int[][] neighbors = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
                for (int[] neighbor : neighbors) {
                    int nx = current.getX() + neighbor[0];
                    int nz = current.getZ() + neighbor[1];
                    
                    // Find if this neighbor exists in our claims
                    for (VClaim other : claims) {
                        if (!processed.contains(other) && other.getX() == nx && other.getZ() == nz) {
                            queue.add(other);
                            processed.add(other);
                            break;
                        }
                    }
                }
            }
            
            regions.add(region);
        }
        
        return regions;
    }
    
    /**
     * Create a marker for a contiguous region of chunks
     */
    private Object createRegionMarker(Village village, List<VClaim> region, Object color) throws Exception {
        // Create the merged shape outline
        Object shape = createMergedShape(region);
        
        // Calculate center position for the marker
        double centerX = 0;
        double centerZ = 0;
        for (VClaim claim : region) {
            centerX += claim.getX() * 16 + 8;
            centerZ += claim.getZ() * 16 + 8;
        }
        centerX /= region.size();
        centerZ /= region.size();
        
        // Create marker: new ShapeMarker(label, shape, shapeY)
        Constructor<?> markerConstructor = shapeMarkerClass.getConstructor(String.class, shapeClass, float.class);
        Object marker = markerConstructor.newInstance(village.getTownName(), shape, 64f);
        
        // Build detail HTML
        StringBuilder detail = new StringBuilder();
        detail.append("<h3>").append(village.getTownName()).append("</h3>");
        detail.append("<p>Owner: ").append(plugin.getUsernameFromUUID(village.getOwner())).append("</p>");
        detail.append("<p>Members: ").append(village.getMembers().length + village.getAssistants().length + 1).append("</p>");
        detail.append("<p>Total Claims: ").append(village.getTotalClaims()).append("</p>");
        detail.append("<p>Region Size: ").append(region.size()).append(" chunks</p>");
        
        Method setDetailMethod = marker.getClass().getMethod("setDetail", String.class);
        setDetailMethod.invoke(marker, detail.toString());
        
        // Set colors
        Method getRedMethod = color.getClass().getMethod("getRed");
        Method getGreenMethod = color.getClass().getMethod("getGreen");
        Method getBlueMethod = color.getClass().getMethod("getBlue");
        
        int r = (Integer) getRedMethod.invoke(color);
        int g = (Integer) getGreenMethod.invoke(color);
        int b = (Integer) getBlueMethod.invoke(color);
        
        Constructor<?> colorConstructor = colorClass.getConstructor(int.class, int.class, int.class, float.class);
        Object fillColor = colorConstructor.newInstance(r, g, b, 0.3f);
        Object lineColor = colorConstructor.newInstance(r, g, b, 0.8f);
        
        Method setFillColorMethod = marker.getClass().getMethod("setFillColor", colorClass);
        Method setLineColorMethod = marker.getClass().getMethod("setLineColor", colorClass);
        Method setLineWidthMethod = marker.getClass().getMethod("setLineWidth", int.class);
        Method setDepthTestEnabledMethod = marker.getClass().getMethod("setDepthTestEnabled", boolean.class);
        
        setFillColorMethod.invoke(marker, fillColor);
        setLineColorMethod.invoke(marker, lineColor);
        setLineWidthMethod.invoke(marker, 2);
        setDepthTestEnabledMethod.invoke(marker, false);
        
        return marker;
    }
    
    /**
     * Create a merged shape from multiple chunks by tracing the outline
     */
    private Object createMergedShape(List<VClaim> claims) throws Exception {
        // Create a set of chunk coordinates for quick lookup
        Set<String> chunkSet = new HashSet<>();
        for (VClaim claim : claims) {
            chunkSet.add(claim.getX() + "," + claim.getZ());
        }
        
        // Find all edge segments (boundaries between claimed and unclaimed)
        Map<String, Set<String>> edges = new HashMap<>();
        
        for (VClaim claim : claims) {
            int cx = claim.getX();
            int cz = claim.getZ();
            
            // Check each of the 4 sides of this chunk
            // If there's no neighbor chunk, this side is part of the outline
            
            // North side (z-)
            if (!chunkSet.contains(cx + "," + (cz - 1))) {
                addEdge(edges, cx * 16, cz * 16, (cx + 1) * 16, cz * 16);
            }
            // South side (z+)
            if (!chunkSet.contains(cx + "," + (cz + 1))) {
                addEdge(edges, cx * 16, (cz + 1) * 16, (cx + 1) * 16, (cz + 1) * 16);
            }
            // West side (x-)
            if (!chunkSet.contains((cx - 1) + "," + cz)) {
                addEdge(edges, cx * 16, cz * 16, cx * 16, (cz + 1) * 16);
            }
            // East side (x+)
            if (!chunkSet.contains((cx + 1) + "," + cz)) {
                addEdge(edges, (cx + 1) * 16, cz * 16, (cx + 1) * 16, (cz + 1) * 16);
            }
        }
        
        // Trace the outline to create a polygon
        List<double[]> points = traceOutline(edges);
        
        // Create Shape using Builder pattern via reflection
        Class<?> vector2dClass = Class.forName("com.flowpowered.math.vector.Vector2d");
        Constructor<?> vector2dConstructor = vector2dClass.getConstructor(double.class, double.class);
        
        // Get Shape.builder()
        Method builderMethod = shapeClass.getMethod("builder");
        Object builder = builderMethod.invoke(null);
        
        // Get Builder.addPoint() method
        Method addPointMethod = builder.getClass().getMethod("addPoint", vector2dClass);
        
        // Add all points to the builder
        for (double[] point : points) {
            Object vector = vector2dConstructor.newInstance(point[0], point[1]);
            addPointMethod.invoke(builder, vector);
        }
        
        // Build the shape
        Method buildMethod = builder.getClass().getMethod("build");
        return buildMethod.invoke(builder);
    }
    
    /**
     * Add an edge to the edge map
     */
    private void addEdge(Map<String, Set<String>> edges, int x1, int z1, int x2, int z2) {
        String point1 = x1 + "," + z1;
        String point2 = x2 + "," + z2;
        
        edges.computeIfAbsent(point1, k -> new HashSet<>()).add(point2);
        edges.computeIfAbsent(point2, k -> new HashSet<>()).add(point1);
    }
    
    /**
     * Trace the outline of the shape from the edge map
     */
    private List<double[]> traceOutline(Map<String, Set<String>> edges) {
        List<double[]> outline = new ArrayList<>();
        
        if (edges.isEmpty()) return outline;
        
        // Start from any point
        String start = edges.keySet().iterator().next();
        String current = start;
        String previous = null;
        
        do {
            // Parse current point
            String[] parts = current.split(",");
            outline.add(new double[]{Double.parseDouble(parts[0]), Double.parseDouble(parts[1])});
            
            // Find next point (not the one we came from)
            Set<String> neighbors = edges.get(current);
            String next = null;
            for (String neighbor : neighbors) {
                if (!neighbor.equals(previous)) {
                    next = neighbor;
                    break;
                }
            }
            
            previous = current;
            current = next;
        } while (current != null && !current.equals(start));
        
        return outline;
    }
    
    /**
     * Create a Color object using reflection
     */
    private Object createColor(int colorInt) throws Exception {
        int r = (colorInt >> 16) & 0xFF;
        int g = (colorInt >> 8) & 0xFF;
        int b = colorInt & 0xFF;
        
        Constructor<?> colorConstructor = colorClass.getConstructor(int.class, int.class, int.class);
        return colorConstructor.newInstance(r, g, b);
    }
    
    /**
     * Get or generate a consistent color for a village
     */
    private int getVillageColor(UUID villageUUID) {
        String uuidString = villageUUID.toString();
        
        if (villageColors.containsKey(uuidString)) {
            return villageColors.get(uuidString);
        }
        
        // Generate a deterministic color based on UUID
        colorRandom.setSeed(villageUUID.getMostSignificantBits() ^ villageUUID.getLeastSignificantBits());
        
        // Generate bright, saturated colors
        float hue = colorRandom.nextFloat();
        float saturation = 0.7f + colorRandom.nextFloat() * 0.3f; // 70-100%
        float brightness = 0.7f + colorRandom.nextFloat() * 0.3f; // 70-100%
        
        int rgb = java.awt.Color.HSBtoRGB(hue, saturation, brightness);
        
        villageColors.put(uuidString, rgb);
        return rgb;
    }
    
    /**
     * Update markers for a specific village
     */
    public void updateVillageMarkers(Village village) {
        if (!enabled) return;
        
        // Update all worlds where this village has claims
        for (String world : village.getWorldsWithClaims()) {
            updateMarkersForWorld(world);
        }
    }
    
    /**
     * Remove markers for a deleted village
     */
    public void removeVillageMarkers(Village village) {
        if (!enabled) return;
        
        // Update all worlds to remove the village's markers
        for (String world : village.getWorldsWithClaims()) {
            updateMarkersForWorld(world);
        }
        
        // Remove cached color
        villageColors.remove(village.getTownUUID().toString());
    }
    
    /**
     * Start automatic marker updates every 5 seconds
     */
    private void startAutoUpdate() {
        if (updateTaskId != -1) {
            // Already running
            return;
        }
        
        // Schedule repeating task (20 ticks = 1 second, so 100 ticks = 5 seconds)
        updateTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (enabled) {
                    updateAllMarkers();
                }
            }
        }, 100L, 100L);
        
        plugin.logger(Level.INFO, "BlueMap auto-update started (every 5 seconds)");
    }
    
    /**
     * Stop automatic marker updates
     */
    public void stopAutoUpdate() {
        if (updateTaskId != -1) {
            Bukkit.getScheduler().cancelTask(updateTaskId);
            updateTaskId = -1;
            plugin.logger(Level.INFO, "BlueMap auto-update stopped");
        }
    }
    
    public boolean isEnabled() {
        return enabled;
    }
}
