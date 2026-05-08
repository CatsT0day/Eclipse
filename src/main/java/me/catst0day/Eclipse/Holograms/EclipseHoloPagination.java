package me.catst0day.Eclipse.Holograms;

import me.catst0day.Eclipse.Eclipse;
import org.bukkit.entity.Player;

import java.util.List;

public class EclipseHoloPagination {
    private final EclipseHologram hologram;
    private final List<String> allLines;
    private final int linesPerPage;
    private int currentPage;
    private final long switchInterval;
    private long lastSwitchTime;
    
    public EclipseHoloPagination(EclipseHologram hologram, List<String> allLines, int linesPerPage, long switchInterval) {
        this.hologram = hologram;
        this.allLines = allLines;
        this.linesPerPage = linesPerPage;
        this.currentPage = 0;
        this.switchInterval = switchInterval;
        this.lastSwitchTime = System.currentTimeMillis();
    }
    
    public void update() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSwitchTime >= switchInterval) {
            nextPage();
            lastSwitchTime = currentTime;
        }
    }
    
    public void nextPage() {
        int totalPages = getTotalPages();
        currentPage = (currentPage + 1) % totalPages;
        updateHologramLines();
    }
    
    public void previousPage() {
        int totalPages = getTotalPages();
        currentPage = (currentPage - 1 + totalPages) % totalPages;
        updateHologramLines();
    }
    
    public void setPage(int page) {
        int totalPages = getTotalPages();
        if (page >= 0 && page < totalPages) {
            currentPage = page;
            updateHologramLines();
        }
    }
    
    public int getCurrentPage() {
        return currentPage;
    }
    
    public int getTotalPages() {
        return (int) Math.ceil((double) allLines.size() / linesPerPage);
    }
    
    public List<String> getCurrentLines() {
        int startIndex = currentPage * linesPerPage;
        int endIndex = Math.min(startIndex + linesPerPage, allLines.size());
        return allLines.subList(startIndex, endIndex);
    }
    
    private void updateHologramLines() {
        hologram.setLines(getCurrentLines());
    }
    
    public void showPageIndicator(Player player) {
        if (getTotalPages() > 1) {
            String indicator = Eclipse.getI().getMessage("hologramPageIndicator")
                    .replace("%current%", String.valueOf(currentPage + 1))
                    .replace("%total%", String.valueOf(getTotalPages()));
            player.sendMessage(indicator);
        }
    }
    
    public boolean hasNextPage() {
        return currentPage < getTotalPages() - 1;
    }
    
    public boolean hasPreviousPage() {
        return currentPage > 0;
    }
    
    public void reset() {
        currentPage = 0;
        lastSwitchTime = System.currentTimeMillis();
        updateHologramLines();
    }
}
