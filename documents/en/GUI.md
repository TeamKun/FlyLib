# GUI System

FlyLib3 has simple gui system that allows to create custom gui.<br/>

# Type of GUI

| *FullClassName*                                 | *VanillaGUIType*      | *Description*               | *isAbstract* |
|-------------------------------------------------|-----------------------|-----------------------------|--------------|
| `com.flylib.flylib3.gui.FGUIComponent`          | `All(Base)`           | Base GUI Class of All       | `true`       |
| `com.flylib.flylib3.gui.inventory.ChestGUI`     | `Chest`               | GUI Class on Chest          | `false`      |
| `com.flylib.flylib3.gui.inventory.InventotyGUI` | `Inventory`           | Base GUI Class on Inventory | `true`       |
| `com.flylib.flylib3.gui.trade.TradeGUI`         | `TradeView(Merchant)` | GUI Class on Trading        | `false`      |

# Basics of GUI

There is pos that express specific position on GUI.<br/>
The type of pos in gui is based on `com.flylib.flylib3.gui.Pos`<br/>
Usually,the parameters of the pos are Int.<br/>

## TradeGUI

For TradeGUI,There are many notes.<br/>

About index of pos,the result view of this gui is sorted by x of pos.<br/>
Even if there is a blank between before entry and after entry,the blank will be ignored.<br/>

For setting value,please Use `com.flylib.flylib3.gui.trade.Trading`.<br/>
Setting with `MerchantRecipe` is deprecated.<br/>
Because it is not requiring ingredient at least one.<br/>