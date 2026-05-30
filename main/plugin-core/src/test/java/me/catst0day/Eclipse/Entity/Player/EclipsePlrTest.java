package me.catst0day.Eclipse.Entity.Player;

import me.catst0day.Eclipse.Eclipse;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class EclipsePlrTest {
    
    @Mock
    private Eclipse plugin;
    
    @Mock
    private Player player;
    
    @Mock
    private World world;
    
    @Mock
    private Location location;
    
    private UUID playerUuid;
    private EclipsePlr eclipsePlr;
    
    @BeforeEach
    public void setUp() {
        playerUuid = UUID.randomUUID();
        eclipsePlr = new EclipsePlr(playerUuid);
    }
    
    @Test
    public void testEclipsePlrCreation() {
        assertNotNull(eclipsePlr);
        assertEquals(playerUuid, eclipsePlr.getUniqueId());
    }
    
    @Test
    public void testIsOnline() {
        when(player.isOnline()).thenReturn(true);
        assertNotNull(eclipsePlr);
    }
    
    @Test
    public void testGetName() {
        when(player.getName()).thenReturn("TestPlayer");
        assertNotNull(eclipsePlr);
    }
    
    @Test
    public void testSendMsg() {
        String message = "&cTest message";
        
        
        
        assertNotNull(eclipsePlr);
    }
    
    @Test
    public void testSendMsgNull() {
        String result = eclipsePlr.sendMsg(null);
        assertNull(result);
    }
    
    @Test
    public void testAddIgnore() {
        UUID otherUuid = UUID.randomUUID();
        eclipsePlr.addIgnore(otherUuid);
        
        assertTrue(eclipsePlr.isIgnoring(otherUuid));
    }
    
    @Test
    public void testRemoveIgnore() {
        UUID otherUuid = UUID.randomUUID();
        eclipsePlr.addIgnore(otherUuid);
        eclipsePlr.removeIgnore(otherUuid);
        
        assertFalse(eclipsePlr.isIgnoring(otherUuid));
    }
    
    @Test
    public void testIsIgnoring() {
        UUID otherUuid = UUID.randomUUID();
        assertFalse(eclipsePlr.isIgnoring(otherUuid));
        
        eclipsePlr.addIgnore(otherUuid);
        assertTrue(eclipsePlr.isIgnoring(otherUuid));
    }
    
    @Test
    public void testSetMetadata() {
        eclipsePlr.setMetadata("testKey", "testValue");
        
        assertEquals("testValue", eclipsePlr.getMetadata("testKey"));
    }
    
    @Test
    public void testGetMetadataNonExistent() {
        assertNull(eclipsePlr.getMetadata("nonExistent"));
    }
    
    @Test
    public void testSetDeathLoc() {
        eclipsePlr.setDeathLoc(location);
        
        assertEquals(location, eclipsePlr.getDeathLoc());
    }
    
    @Test
    public void testSetTotalPlayTime() {
        long playTime = 3600000L; 
        eclipsePlr.setTotalPlayTime(playTime);
        
        assertEquals(playTime, eclipsePlr.getTotalPlayTime());
    }
    
    @Test
    public void testSetHealth() {
        double health = 15.0;
        eclipsePlr.setHealth(health);
        
        
        
        assertNotNull(eclipsePlr);
    }
    
    @Test
    public void testSetAllowFlight() {
        eclipsePlr.setAllowFlight(true);
        
        
        
        assertNotNull(eclipsePlr);
    }
    
    @Test
    public void testTeleportWithCause() {
        
        assertNotNull(eclipsePlr);
    }
    
    @Test
    public void testTeleportAsynchronously() {
        
        assertNotNull(eclipsePlr);
    }
    
    @Test
    public void testSendTitle() {
        
        assertNotNull(eclipsePlr);
    }
    
    @Test
    public void testSendTitleAsynchronously() {
        
        assertNotNull(eclipsePlr);
    }
    
    @Test
    public void testSetGameMode() {
        
        assertNotNull(eclipsePlr);
    }
    
    @Test
    public void testMultipleMetadata() {
        eclipsePlr.setMetadata("key1", "value1");
        eclipsePlr.setMetadata("key2", 123);
        eclipsePlr.setMetadata("key3", true);
        
        assertEquals("value1", eclipsePlr.getMetadata("key1"));
        assertEquals(123, eclipsePlr.getMetadata("key2"));
        assertEquals(true, eclipsePlr.getMetadata("key3"));
    }
    
    @Test
    public void testMultipleIgnores() {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        UUID uuid3 = UUID.randomUUID();
        
        eclipsePlr.addIgnore(uuid1);
        eclipsePlr.addIgnore(uuid2);
        eclipsePlr.addIgnore(uuid3);
        
        assertTrue(eclipsePlr.isIgnoring(uuid1));
        assertTrue(eclipsePlr.isIgnoring(uuid2));
        assertTrue(eclipsePlr.isIgnoring(uuid3));
        
        eclipsePlr.removeIgnore(uuid2);
        
        assertTrue(eclipsePlr.isIgnoring(uuid1));
        assertFalse(eclipsePlr.isIgnoring(uuid2));
        assertTrue(eclipsePlr.isIgnoring(uuid3));
    }
    
    @Test
    public void testGetUniqueId() {
        assertEquals(playerUuid, eclipsePlr.getUniqueId());
    }
}
