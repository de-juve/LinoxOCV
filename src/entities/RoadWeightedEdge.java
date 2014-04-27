package entities;

import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * Определим веса ребер для графа
 */
public class RoadWeightedEdge extends DefaultEdge
{


   // private static final long serialVersionUID = 229708706467350994L;



    double weight = WeightedGraph.DEFAULT_EDGE_WEIGHT;



    /**
     * Retrieves the weight of this edge. This is protected, for use by
     * subclasses only (e.g. for implementing toString).
     *
     * @return weight of this edge
     */
    protected double getWeight()
    {
        return weight;
    }
}