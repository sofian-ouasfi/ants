import java.util.*;
import java.io.*;
import java.lang.annotation.Target;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
 

class Player {

    private static HashMap<Integer,Cell> cells;

    private static int totalEggs = 0;
    private static int totalCristals = 0;
    private static int availableEggs = 0;
    private static int availableCristals = 0;
    

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int numberOfCells = in.nextInt(); // amount of hexagonal cells in this map
        cells = new HashMap<>();
        //Set<Cell> targetCells = new HashSet<>();
        for (int i = 0; i < numberOfCells; i++) {
            int resourceType = in.nextInt(); // 0 for empty, 1 for eggs, 2 for crystal
            int resourceNumber = in.nextInt(); // the initial amount of eggs/crystals on this cell
            if(resourceNumber>0 && resourceType==1){
                totalEggs += resourceNumber;
            }
            if(resourceNumber>0 && resourceType == 2){
                totalCristals += resourceNumber;
            }
            List<Integer> adjacentCells = new ArrayList<Integer>(); // the index of the neighbouring cell for each direction
            for(int j=0; j<=5; j++){
                int neigh = in.nextInt();
                if(neigh!=-1){
                    adjacentCells.add(neigh);
                }
            }
            Cell currentCell = new Cell(i, resourceType, resourceNumber, adjacentCells);
            cells.put(i, currentCell);
        }

        /* 
        for (int i=0; i<cells.size(); i++) {
            System.err.println("INDEX "+i+" - "+cells.get(i).toString());
        }
        */

        int numberOfBases = in.nextInt();
        int myBaseIndex = 0, oppBaseIndex = 0;
        for (int i = 0; i < numberOfBases; i++) {
            myBaseIndex = in.nextInt();
        }
        for (int i = 0; i < numberOfBases; i++) {
            oppBaseIndex = in.nextInt();
        }
        availableEggs = totalEggs;
        availableCristals = totalCristals;
        
        HashMap<Integer, List<Cell>> myGraph = createGraphFromBase(myBaseIndex);
        //HashMap<Integer, List<Cell>> oppGaph = createGraphFromBase(oppBaseIndex);
        System.err.println("NUMBER OF CELLS : " + cells.size());
        System.err.println("---------------");
        System.err.println("PLAYER BASE : " + myBaseIndex);
        System.err.println("OPPONENT BASE : " + oppBaseIndex);
        System.err.println("---------------");
        System.err.println("TOTAL EGGS : " + totalEggs);
        System.err.println("TOTAL CRISTALS : " + totalCristals);
        
        for (int i = 0; i < myGraph.size(); i++) {
            System.err.println("\nTREE LEVEL "+i+" :"+myGraph.get(i).toString()+"\n");
        }

        // game loop
        while (true) {
            for (int i = 0; i < cells.size(); i++) {
                int resources = in.nextInt(); // the current amount of eggs/crystals on this cell
                int myAnts = in.nextInt(); // the amount of your ants on this cell
                int oppAnts = in.nextInt(); // the amount of opponent ants on this cell
                int delta = cells.get(i).updateValues(resources, myAnts, oppAnts);
                if(cells.get(i).getResourceType()==Cell.CellType.EGGS){
                    availableEggs -= delta;
                }else if(cells.get(i).getResourceType()==Cell.CellType.CRISTALS){
                    availableCristals -= delta;
                }
                System.err.println("---------------");
                System.err.println("AVAILABLE EGGS : " + availableEggs);
                System.err.println("AVAILABLE CRISTALS : " + availableCristals);
                //System.err.println(cells.get(i).toString());
            }
            int levelEgg = 0, levelCristal = 0;
            boolean stop = false;
            while(!stop){
                List<Integer> eggs = findTargets(myGraph, levelEgg, Cell.CellType.EGGS);
                if(!eggs.isEmpty()){
                    String finalString = "";
                    for(int j = 0; j < eggs.size(); j++){ 
                        finalString += "LINE "+myBaseIndex+" "+eggs.get(j)+" "+(100/eggs.size())+";";
                    }
                    stop = true;
                    System.out.println(finalString);
                }else{
                    //System.err.println("NO TARGET FOR LEVEL "+level);
                    levelEgg++;
                }

                List<Integer> targets = findTargets(myGraph, levelCristal, Cell.CellType.ALL);
                if(!targets.isEmpty()){
                    String finalString = "";
                    for(int j = 0; j < targets.size(); j++){ 
                        finalString += "LINE "+myBaseIndex+" "+targets.get(j)+" "+(100/targets.size())+";";
                    }
                    stop = true;
                    System.out.println(finalString);
                }else{
                    System.err.println("NO TARGET FOR LEVEL "+levelCristal);
                    levelCristal++;
                }
            }

            
            
            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");


            // WAIT | LINE <sourceIdx> <targetIdx> <strength> | BEACON <cellIdx> <strength> | MESSAGE <text>
        }
    }

    private static HashMap<Integer, List<Cell>> createGraphFromBase(int baseIndex) {
        Cell baseCell = cells.get(baseIndex);
        HashMap<Integer, List<Cell>> graph = new HashMap<>();
        graph = insertIntoLevel(graph, 0, baseCell, -1);
        
        List<Cell> tempList = new ArrayList<>();
        tempList.add(baseCell);
        int currentLevel = 0;
        
        while(tempList != null){
            graph = createSubGraph(graph, currentLevel, tempList);
            currentLevel++;    
            tempList = graph.get(currentLevel);
        }
        return graph;
    }

    private static HashMap<Integer, List<Cell>> createSubGraph(HashMap<Integer, List<Cell>> graph, int level, List<Cell> cellList) {
        for (Cell cell : cellList) {
            // ADD ADJ CELLS
            for (int i=0; i<cell.getAdjacentCells().size(); i++) {
                Cell adjacentCell = cells.get(cell.getAdjacentCells().get(i));
                graph = insertIntoLevel(graph, level+1, adjacentCell, cell.getIndex());
            }
        }
        return graph;
    }

    private static HashMap<Integer, List<Cell>> insertIntoLevel(HashMap<Integer, List<Cell>> graph, int level, Cell cell, int parent) {
        boolean shouldAdd = true;
        for(int i=0; i<graph.size(); i++){
            List<Cell> cellList = graph.get(i);
            if(containsCell(cellList,cell)){
                shouldAdd = false;
                if(i==level){
                    System.err.println("LEVEL "+ i +" - CELL "+cell.getIndex()+" HAS PARENT " + cell.getParentCells().toString() +" / CURRENT PARENT IS "+parent);
                }
                break;
            }else{
                //System.err.println("ADD " + cell.index + " TO LEVEL " + level);
            }
        }
        if(shouldAdd){
            if(graph.get(level)==null){
                graph.put(level, new ArrayList<Cell>());
            }
            List<Cell> newList = graph.get(level);
            cell.addParentCell(parent);
            newList.add(cell);
            graph.replace(level, newList);
            //System.err.println("LEVEL " + level + " - " + newList.toString()); 
        }
        return graph;
    }

    private static boolean containsCell(List<Cell> cellList, Cell cell) {

        for(int i=0; i < cellList.size(); i++){
            if(cellList.get(i).getIndex()==cell.getIndex()){
                return true;
            }
        }
        return false;
    }

    private static List<Integer> findTargets(HashMap<Integer, List<Cell>> myGraph, int level, Cell.CellType cellType){

        List<Cell> targetedCells = myGraph.get(level);
        List<Integer> finalCells = new ArrayList<>();
        for (Cell cell : targetedCells) {
            boolean isTarget = false;
            switch (cellType) {
                case ALL:
                    isTarget = cell.getResourceType() == Cell.CellType.EGGS || cell.getResourceType() == Cell.CellType.CRISTALS;
                    break;
                case EGGS:
                    isTarget = cell.getResourceType() == Cell.CellType.EGGS && level < 5;
                    break;
                case CRISTALS:
                    isTarget = cell.getResourceType() == Cell.CellType.CRISTALS;
                    break;
                default:
                    System.err.println("TARGET INCONNUE");
                    break;
            }
            if(isTarget && cell.getResourceNumber()>0 && !finalCells.contains(cell.getIndex())){
                System.err.println("FIND TARGET LEVEL "+ level +" \n" + cell.toString());
                finalCells.add(cell.getIndex());
            }    
        }
        return finalCells;
    }
}

class Cell {

    public enum CellType {
        NONE(0),
        EGGS(1),
        CRISTALS(2),
        ALL(3);

        private final int value;

        private CellType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private int index, myAnts, oppAnts, resourceNumber;

    private CellType resourceType;
    private List<Integer> adjacentCells;
    private List<Integer> parentCells;

    public Cell(int i, int resourceType, int resourceNumber, List<Integer> adjacentCells){
        this.index = i;
        switch (resourceType) {
            case 0:
                this.resourceType = CellType.NONE;
                break;
            case 1:
                this.resourceType = CellType.EGGS;
                break;
            case 2:
                this.resourceType = CellType.CRISTALS;
                break;
        }
        this.resourceNumber = resourceNumber;
        this.adjacentCells = adjacentCells;
        this.parentCells = new ArrayList<>();
    }

    public int updateValues(int resourceNumber, int myAnts, int oppAnts){
        int delta = 0;
        if(this.resourceNumber != resourceNumber){
            delta = this.resourceNumber-resourceNumber;
            this.resourceNumber = resourceNumber;
        }
        this.myAnts = myAnts;
        this.oppAnts = oppAnts;
        return delta;
    }

    public String toString(){
        return "\n\t | CELL "+ this.index + " (P="+this.parentCells.toString()+") : RESOURCE "+ this.resourceType + (this.resourceType==CellType.NONE ? "" : " / NUMBER "+this.resourceNumber) + " / NEIGHBORS "+this.adjacentCells.toString();
    }

    public int getIndex(){
        return this.index;
    }

    public void setIndex(int index){
        this.index = index;
    }

    public int getMyAnts(){
        return this.myAnts;
    }

    public void setMyAnts(int myAnts){
        this.myAnts = myAnts;
    }

    public int getOppAnts(){
        return this.oppAnts;
    }

    public void setOppAnts(int oppAnts){
        this.oppAnts = oppAnts;
    }

    public CellType getResourceType(){
        return this.resourceType;
    }

    public void setResourceType(CellType resourceType){
        this.resourceType = resourceType;
    }
    
    public int getResourceNumber() {
        return resourceNumber;
    }

    public void setResourceNumber(int resourceNumber) {
        this.resourceNumber = resourceNumber;
    }

    public List<Integer> getAdjacentCells() {
        return adjacentCells;
    }

    public List<Integer> getParentCells() {
        return parentCells;
    }

    public void addAdjacentCell(int index){
        this.adjacentCells.add(index);
    }

    public void addParentCell(int index){
        this.parentCells.add(index);
    }
}
