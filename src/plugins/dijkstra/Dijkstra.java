package plugins.dijkstra;

import entities.PixelsMentor;
import entities.Point;
import org.opencv.core.Mat;

import java.util.*;

public class Dijkstra {
    public static HashMap<Point, Vertex> createVertexes( Mat image ) {
        HashMap<Point, Vertex> vertexes = new HashMap<>();
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<>();
        vertexQueue.add( new Vertex( new Point( 0, 0 ) ) );

        while ( !vertexQueue.isEmpty() ) {
            Vertex vertex = vertexQueue.poll();
            if ( vertex.label ) {
                continue;
            }
            ArrayList<Point> neighbors = PixelsMentor.getNeighborhoodOfPixel( vertex.point.x, vertex.point.y, image, 1 );
            List<Edge> edges = new ArrayList<>();
            for ( Point n : neighbors ) {
                Vertex nv = new Vertex( n );
                double weight = Math.sqrt( Math.pow( vertex.point.x - n.x, 2 ) + Math.pow( vertex.point.y - n.y, 2 ) );
                edges.add( new Edge( nv, weight ) );//W.get( vertex.point ).get( nv.point.y, nv.point.x )[0]) );
                if ( !nv.label ) {
                    vertexQueue.add( nv );
                }
            }
            vertex.adjacencies = new Edge[edges.size()];
            edges.toArray( vertex.adjacencies );
            vertex.label = true;
            vertexes.put( vertex.point, vertex );
        }
        return vertexes;
    }

    public static void computePaths( Vertex source, HashMap<entities.Point, Vertex> vertexList ) {
        source.minDistance = 0.;
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<>();
        vertexQueue.add( source );

        while ( !vertexQueue.isEmpty() ) {
            Vertex u = vertexQueue.poll();

            // Visit each edge exiting u
            for ( Edge e : u.adjacencies ) {
                Vertex v = e.target;
                double weight = e.weight;
                double distanceThroughU = u.minDistance + weight;
                if ( distanceThroughU < v.minDistance ) {
                    vertexQueue.remove( v );
                    v.minDistance = distanceThroughU;
                    v.previous = u;
                    vertexList.get( v.point ).minDistance = v.minDistance;
                    vertexQueue.add( v );
                }
            }
        }
    }

    public static List<Vertex> getShortestPathTo( Vertex target ) {
        List<Vertex> path = new ArrayList<>();
        for ( Vertex vertex = target; vertex != null; vertex = vertex.previous )
            path.add( vertex );
        Collections.reverse( path );
        return path;
    }
}

