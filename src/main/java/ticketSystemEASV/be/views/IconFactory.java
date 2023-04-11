package ticketSystemEASV.be.views;

import javafx.scene.image.Image;

import java.util.HashMap;

public class IconFactory {
    private HashMap<Icon, Image> iconCache = new HashMap<>();
    private HashMap<Icon, String> iconPathCache = new HashMap<>();
    public enum Icon {NAME, MAP_PIN, CLOCK, CALENDAR}

    public IconFactory() {
        iconPathCache.put(Icon.NAME, "/images/icons/rename.png");
        iconPathCache.put(Icon.MAP_PIN, "/images/icons/map-pin.png");
        iconPathCache.put(Icon.CLOCK, "/images/icons/clock.png");
        iconPathCache.put(Icon.CALENDAR, "/images/icons/calendar.png");
    }

    public Image create(Icon icon) {
        Image image = iconCache.get(icon);
        if (image == null) {
            image = new Image(iconPathCache.get(icon), 50, 50, true, true);
            iconCache.put(icon, image);
        }
        return image;
    }
}
