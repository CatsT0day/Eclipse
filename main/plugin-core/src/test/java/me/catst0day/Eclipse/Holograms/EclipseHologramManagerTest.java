package me.catst0day.Eclipse.Holograms;

import me.catst0day.Eclipse.Eclipse;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class EclipseHologramManagerTest {
    
    @Mock
    private Eclipse plugin;
    
    @Mock
    private World world;
    
    @Mock
    private Location location;
    
    private EclipseHologramManager hologramManager;
    
    @BeforeEach
    public void setUp() {
        
        when(plugin.getDataFolder()).thenReturn(new java.io.File("test"));
        hologramManager = new EclipseHologramManager(plugin);
    }
    
    @Test
    public void testCreateHologram() {
        List<String> lines = new ArrayList<>();
        lines.add("Test Line");
        
        boolean result = hologramManager.createHologram("testHolo", location, lines);
        
        assertTrue(result);
        assertNotNull(hologramManager.getHologram("testHolo"));
    }
    
    @Test
    public void testCreateDuplicateHologram() {
        List<String> lines = new ArrayList<>();
        lines.add("Test Line");
        
        hologramManager.createHologram("testHolo", location, lines);
        boolean result = hologramManager.createHologram("testHolo", location, lines);
        
        assertFalse(result);
    }
    
    @Test
    public void testDeleteHologram() {
        List<String> lines = new ArrayList<>();
        lines.add("Test Line");
        
        hologramManager.createHologram("testHolo", location, lines);
        boolean result = hologramManager.deleteHologram("testHolo");
        
        assertTrue(result);
        assertNull(hologramManager.getHologram("testHolo"));
    }
    
    @Test
    public void testDeleteNonExistentHologram() {
        boolean result = hologramManager.deleteHologram("nonExistent");
        
        assertFalse(result);
    }
    
    @Test
    public void testGetHologram() {
        List<String> lines = new ArrayList<>();
        lines.add("Test Line");
        
        hologramManager.createHologram("testHolo", location, lines);
        EclipseHologram hologram = hologramManager.getHologram("testHolo");
        
        assertNotNull(hologram);
        assertEquals("testHolo", hologram.getName());
    }
    
    @Test
    public void testGetHologramCaseInsensitive() {
        List<String> lines = new ArrayList<>();
        lines.add("Test Line");
        
        hologramManager.createHologram("TestHolo", location, lines);
        
        assertNotNull(hologramManager.getHologram("testholo"));
        assertNotNull(hologramManager.getHologram("TESTHOLO"));
        assertNotNull(hologramManager.getHologram("TestHolo"));
    }
    
    @Test
    public void testGetAllHolograms() {
        List<String> lines = new ArrayList<>();
        lines.add("Test Line");
        
        hologramManager.createHologram("holo1", location, lines);
        hologramManager.createHologram("holo2", location, lines);
        
        assertEquals(2, hologramManager.getAllHolograms().size());
    }
    
    @Test
    public void testGetHologramNames() {
        List<String> lines = new ArrayList<>();
        lines.add("Test Line");
        
        hologramManager.createHologram("holo1", location, lines);
        hologramManager.createHologram("holo2", location, lines);
        
        List<String> names = hologramManager.getHologramNames();
        
        assertEquals(2, names.size());
        assertTrue(names.contains("holo1"));
        assertTrue(names.contains("holo2"));
    }
    
    @Test
    public void testHologramExists() {
        List<String> lines = new ArrayList<>();
        lines.add("Test Line");
        
        hologramManager.createHologram("testHolo", location, lines);
        
        assertTrue(hologramManager.hologramExists("testHolo"));
        assertFalse(hologramManager.hologramExists("nonExistent"));
    }
    
    @Test
    public void testUpdateHologram() {
        List<String> lines = new ArrayList<>();
        lines.add("Original Line");
        
        hologramManager.createHologram("testHolo", location, lines);
        EclipseHologram hologram = hologramManager.getHologram("testHolo");
        
        List<String> newLines = new ArrayList<>();
        newLines.add("Updated Line");
        hologram.setLines(newLines);
        
        hologramManager.updateHologram(hologram);
        
        assertEquals("Updated Line", hologram.getLines().get(0));
    }
}
