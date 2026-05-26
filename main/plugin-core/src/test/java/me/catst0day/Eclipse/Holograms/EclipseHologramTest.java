package me.catst0day.Eclipse.Holograms;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EclipseHologram.
 * Tests hologram creation, line management, and settings.
 */
@ExtendWith(MockitoExtension.class)
public class EclipseHologramTest {
    
    @Mock
    private World world;
    
    @Mock
    private Location location;
    
    @Test
    public void testHologramCreation() {
        List<String> lines = new ArrayList<>();
        lines.add("Line 1");
        lines.add("Line 2");
        
        EclipseHologram hologram = new EclipseHologram("testHolo", location, lines);
        
        assertNotNull(hologram);
        assertEquals("testHolo", hologram.getName());
        assertEquals(2, hologram.getLines().size());
        assertNotNull(hologram.getUniqueId());
    }
    
    @Test
    public void testHologramNameSanitization() {
        List<String> lines = new ArrayList<>();
        lines.add("Test");
        
        EclipseHologram hologram1 = new EclipseHologram("Test Holo!", location, lines);
        assertEquals("Test_Holo_", hologram1.getName());
        
        EclipseHologram hologram2 = new EclipseHologram("", location, lines);
        assertTrue(hologram2.getName().startsWith("hologram_"));
    }
    
    @Test
    public void testAddLine() {
        List<String> lines = new ArrayList<>();
        lines.add("Line 1");
        
        EclipseHologram hologram = new EclipseHologram("testHolo", location, lines);
        hologram.addLine("Line 2");
        
        assertEquals(2, hologram.getLines().size());
        assertEquals("Line 2", hologram.getLines().get(1));
    }
    
    @Test
    public void testRemoveLine() {
        List<String> lines = new ArrayList<>();
        lines.add("Line 1");
        lines.add("Line 2");
        lines.add("Line 3");
        
        EclipseHologram hologram = new EclipseHologram("testHolo", location, lines);
        hologram.removeLine(1);
        
        assertEquals(2, hologram.getLines().size());
        assertEquals("Line 1", hologram.getLines().get(0));
        assertEquals("Line 3", hologram.getLines().get(1));
    }
    
    @Test
    public void testSetLine() {
        List<String> lines = new ArrayList<>();
        lines.add("Line 1");
        lines.add("Line 2");
        
        EclipseHologram hologram = new EclipseHologram("testHolo", location, lines);
        hologram.setLine(0, "New Line");
        
        assertEquals("New Line", hologram.getLines().get(0));
        assertEquals("Line 2", hologram.getLines().get(1));
    }
    
    @Test
    public void testSetLines() {
        List<String> originalLines = new ArrayList<>();
        originalLines.add("Line 1");
        
        List<String> newLines = new ArrayList<>();
        newLines.add("New Line 1");
        newLines.add("New Line 2");
        
        EclipseHologram hologram = new EclipseHologram("testHolo", location, originalLines);
        hologram.setLines(newLines);
        
        assertEquals(2, hologram.getLines().size());
        assertEquals("New Line 1", hologram.getLines().get(0));
    }
    
    @Test
    public void testViewDistance() {
        List<String> lines = new ArrayList<>();
        lines.add("Test");
        
        EclipseHologram hologram = new EclipseHologram("testHolo", location, lines);
        hologram.setViewDistance(64);
        
        assertEquals(64, hologram.getViewDistance());
    }
    
    @Test
    public void testViewDistanceClamping() {
        List<String> lines = new ArrayList<>();
        lines.add("Test");
        
        EclipseHologram hologram = new EclipseHologram("testHolo", location, lines);
        hologram.setViewDistance(200); // Should be clamped to 128
        
        assertEquals(128, hologram.getViewDistance());
        
        hologram.setViewDistance(0); // Should be clamped to 1
        assertEquals(1, hologram.getViewDistance());
    }
    
    @Test
    public void testAlwaysVisible() {
        List<String> lines = new ArrayList<>();
        lines.add("Test");
        
        EclipseHologram hologram = new EclipseHologram("testHolo", location, lines);
        hologram.setAlwaysVisible(true);
        
        assertTrue(hologram.isAlwaysVisible());
    }
    
    @Test
    public void testUpdateInterval() {
        List<String> lines = new ArrayList<>();
        lines.add("Test");
        
        EclipseHologram hologram = new EclipseHologram("testHolo", location, lines);
        hologram.setUpdateInterval(20);
        
        assertEquals(20, hologram.getUpdateInterval());
    }
    
    @Test
    public void testUpdateIntervalClamping() {
        List<String> lines = new ArrayList<>();
        lines.add("Test");
        
        EclipseHologram hologram = new EclipseHologram("testHolo", location, lines);
        hologram.setUpdateInterval(0); // Should be clamped to 1
        
        assertEquals(1, hologram.getUpdateInterval());
    }
    
    @Test
    public void testClickable() {
        List<String> lines = new ArrayList<>();
        lines.add("Test");
        
        EclipseHologram hologram = new EclipseHologram("testHolo", location, lines);
        hologram.setClickable(true);
        hologram.setClickCommand("/test command");
        
        assertTrue(hologram.isClickable());
        assertEquals("/test command", hologram.getClickCommand());
    }
    
    @Test
    public void testEnabled() {
        List<String> lines = new ArrayList<>();
        lines.add("Test");
        
        EclipseHologram hologram = new EclipseHologram("testHolo", location, lines);
        hologram.setEnabled(false);
        
        assertFalse(hologram.isEnabled());
    }
    
    @Test
    public void testPermission() {
        List<String> lines = new ArrayList<>();
        lines.add("Test");
        
        EclipseHologram hologram = new EclipseHologram("testHolo", location, lines);
        hologram.setPermission("eclipse.hologram.view");
        
        assertEquals("eclipse.hologram.view", hologram.getPermission());
        assertFalse(hologram.isAlwaysVisible());
    }
    
    @Test
    public void testLineSanitization() {
        List<String> lines = new ArrayList<>();
        lines.add("Line 1\nLine 2");
        lines.add("Line 3\rLine 4");
        
        EclipseHologram hologram = new EclipseHologram("testHolo", location, lines);
        
        assertEquals("Line 1 Line 2", hologram.getLines().get(0));
        assertEquals("Line 3 Line 4", hologram.getLines().get(1));
    }
    
    @Test
    public void testGetLinesForPage() {
        List<String> lines = new ArrayList<>();
        lines.add("Page 1 Line 1");
        lines.add("Page 1 Line 2");
        lines.add("!nextpage!");
        lines.add("Page 2 Line 1");
        lines.add("Page 2 Line 2");
        
        EclipseHologram hologram = new EclipseHologram("testHolo", location, lines);
        
        List<String> page1 = hologram.getLinesForPage(0);
        assertEquals(2, page1.size());
        assertEquals("Page 1 Line 1", page1.get(0));
        
        List<String> page2 = hologram.getLinesForPage(1);
        assertEquals(2, page2.size());
        assertEquals("Page 2 Line 1", page2.get(0));
    }
    
    @Test
    public void testGetPageCount() {
        List<String> lines = new ArrayList<>();
        lines.add("Page 1");
        lines.add("!nextpage!");
        lines.add("Page 2");
        
        EclipseHologram hologram = new EclipseHologram("testHolo", location, lines);
        
        assertEquals(2, hologram.getPageCount());
    }
    
    @Test
    public void testPlayerPageManagement() {
        List<String> lines = new ArrayList<>();
        lines.add("Test");
        
        EclipseHologram hologram = new EclipseHologram("testHolo", location, lines);
        Player player = mock(Player.class);
        UUID playerUuid = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerUuid);
        
        hologram.setPlayerPage(player, 1);
        assertEquals(1, hologram.getPlayerPage(player));
        
        hologram.nextPage(player);
        assertEquals(1, hologram.getPlayerPage(player));
        
        hologram.prevPage(player);
        assertEquals(0, hologram.getPlayerPage(player));
    }
}
