package net.sf.freecol.common.model;

import net.sf.freecol.common.i18n.Messages;
import net.sf.freecol.common.io.FreeColXMLReader;
import net.sf.freecol.common.io.FreeColXMLWriter;
import net.sf.freecol.common.util.Utils;

import javax.xml.stream.XMLStreamException;

public class UnitRecruitable extends Recruitable {

    public static final String TAG = "unitRecruitable";

    /**
     * The unit to be recruited.
     */
    protected Unit unit;
    protected int price;

    /**
     * Creates a new {@code UnitRecruitable} instance.
     *
     * @param game The enclosing {@code Game}.
     * @param source The source {@code Player}.
     * @param destination The destination {@code Player}.
     * @param unit The {@code Unit} to trade.
     */
    public UnitRecruitable(Game game, Player source, Player destination,
                          Unit unit) {
        super(game, Messages.nameKey("model.Recruitable.unit"),
                source, destination);

        this.unit = unit;
        this.price = getUnitPrice(source, unit);
    }


    // Interface TradeItem

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid() {
        return unit != null && unit.getType() != null
                && (unit.getLocation() instanceof Ownable)
                && getSource().owns((Ownable)unit);
    }

    @Override
    public boolean isUnique() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringTemplate getLabel() {
        return unit.getLabel();
    }

    /**
     * {@inheritDoc}
     */
    public int evaluateFor(Player player) {
        final Market market = player.getMarket();
        final Unit unit = getUnit();
        /**TODO: check if this is correct*/
        return (!isValid()) ?
                INVALID_UNIT : ((market == null) ? -2*getUnitPrice(player, unit) : getUnitPrice(player, unit));
    }

    public int getUnitPrice (Player player, Unit unit){
        int OwnerGold = unit.getOwner().getGold();
        double unitPrice = player.gold*0.3 > OwnerGold*0.7 ? OwnerGold*0.60 : player.gold*0.2;
        return (int) unitPrice;
    }

    public boolean priceIsValid(){
        return price > 0;
    }

    // Serialization

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeChildren(FreeColXMLWriter xw) throws XMLStreamException {
        super.writeChildren(xw);

        unit.toXML(xw);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void readChildren(FreeColXMLReader xr) throws XMLStreamException {
        // Clear containers.
        unit = null;

        super.readChildren(xr);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void readChild(FreeColXMLReader xr) throws XMLStreamException {
        final Game game = getGame();
        final String tag = xr.getLocalName();

        if (Unit.TAG.equals(tag)) {
            unit = new Unit(game, xr.readId());

        } else {
            super.readChild(xr);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getXMLTagName() { return TAG; }

    // Override FreeColObject

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends FreeColObject> boolean copyIn(T other) {
        ColonyTradeItem o = copyInCast(other, ColonyTradeItem.class);
        if (o == null || !super.copyIn(o)) return false;
        Unit u = o.getUnit();
        if (u == null) {
            this.unit = null;
        } else if (this.unit == null) {
            this.unit = u;
        } else {
            return this.unit.copyIn(u);
        }
        return true;
    }

    // Override Object

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof UnitRecruitable) {
            return Utils.equals(this.unit, ((UnitRecruitable)other).unit)
                    && super.equals(other);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        return 37 * hash + Utils.hashCode(this.unit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(16);
        sb.append('[').append(getId())
                .append(' ').append(unit.getName()).append(']');
        return sb.toString();
    }

    public void setPrice(int price){
        this.price = price;
    }
}
