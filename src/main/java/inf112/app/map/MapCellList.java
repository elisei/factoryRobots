package inf112.app.map;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import inf112.app.objects.IBoardElement;
import inf112.app.util.ObjectFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Class for generating and holding all the cells in the map
 * Uses the {@link ObjectFactory} to extract the objects from the TiledMap
 * and fill the cells with them.
 */
public class MapCellList {
    private MapCell[][] cellList;
    // Layer we don't want to create objects from
    private ArrayList<String> exclusionList;
    private ObjectFactory factory;

    MapCellList(int sizeX, int sizeY, MapLayers layers){
        cellList = new MapCell[sizeX][sizeY];
        exclusionList = new ArrayList<>(Arrays.asList("Board","Hole"));
        factory = new ObjectFactory();

        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                CellInventory inventory = new CellInventory();
                inventory.setElements(findObjectsInLayers(x, y, layers));
                cellList[x][y] = new MapCell(new Position(x, y), inventory);
            }
        }
    }

    /**
     * Goes through all the layers (not in exclusionList) at a certain
     * coordinate to extract the objects found and return them
     * @param x coordinate
     * @param y coordinate
     * @param layers of the map
     * @return ArrayList of the objects found at the coordinates
     */
    private ArrayList<IBoardElement> findObjectsInLayers(int x, int y, MapLayers layers){
        ArrayList<IBoardElement> elements = new ArrayList<>();
        Iterator<MapLayer> it = layers.iterator();
        while(it.hasNext()){
            TiledMapTileLayer layer = (TiledMapTileLayer) it.next();
            if (layer.getCell(x, y) != null && !exclusionList.contains(layer.getName())){
                TiledMapTile element = layer.getCell(x, y).getTile();
                elements.add(factory.generateObject(element,x,y));

            }
        }
        return elements;
    }

    public MapCell getCell(Position p){
        return cellList[p.getXCoordinate()][p.getYCoordinate()];
    }

    public MapCell getCell(int x, int y){
        Position p = new Position(x,y);
        return getCell(p);
    }

    private void setCell(Position p, CellInventory inventory){
        cellList[p.getXCoordinate()][p.getYCoordinate()] = new MapCell(p, inventory);
    }
    private void setCell(int x, int y, CellInventory inventory){
        Position p = new Position(x,y);
        setCell(p, inventory);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MapCellList)) return false;
        MapCellList that = (MapCellList) o;
        return Arrays.equals(cellList, that.cellList);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(cellList);
    }
}
