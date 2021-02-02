package Bot.Shop;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ShopItemManager {
    private final List<IShopItem> shopItems = new ArrayList<>();

    public ShopItemManager() {
        addShopItem(new Salad());
        addShopItem(new Water());
        addShopItem(new Steak());
        addShopItem(new Lemonade());
        addShopItem(new Cacao());
        addShopItem(new Taco());
    }

    public List<IShopItem> getShopItems() {
        return this.shopItems;
    }

    @Nullable
    public IShopItem getShopItem(String search) {
        String searchLower = search.toLowerCase();

        for (IShopItem item : this.shopItems) {
            if (item.getName().equals(searchLower)) {
                return item;
            }
        }
        return null;
    }

    private void addShopItem(IShopItem item) {
        this.shopItems.add(item);
    }
}
