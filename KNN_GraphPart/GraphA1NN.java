/* Mohamed Nefsi
student number :300305042
*/

/* ---------------------------------------------------------------------------------
The PointSet class that contains an ArraList of LabelledPoint instances.
It also reads fvecs files from corpus-texmex.irisa.fr


(c) Robert Laganiere, CSI2510 2023
------------------------------------------------------------------------------------*/

import java.io.*;
import java.util.*;

class GraphA1NN {
	
	UndirectedGraph<LabelledPoint> annGraph;
    private PointSet dataset;
	private int S; // Capacity of array A
	// construct a graph from a file
    public GraphA1NN(String fvecs_filename) {

	    annGraph= new UndirectedGraph<>();
		dataset= new PointSet(PointSet.read_ANN_SIFT(fvecs_filename));
    }

	// construct a graph from a dataset
    public GraphA1NN(PointSet set){
		
	   annGraph= new UndirectedGraph<>();
       this.dataset = set;
    }

    // build the graph
    
	public void constructKNNGraph(int K,String filename) {
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(":");
				int vertexID = Integer.parseInt(parts[0].trim());
				String[] neighborsStr = parts[1].split(",");

				for (int i = 0; i < Math.min(K, neighborsStr.length); i++) {
					//System.out.print(" ligne size: "+neighborsStr.length);
					int neighborID = Integer.parseInt(neighborsStr[i].trim());
					annGraph.addEdge(dataset.getPointsList().get(vertexID), dataset.getPointsList().get(neighborID));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	//A setter method setS that sets the capacity of the array used to
	// set the value of S, the capacity of array A
	public void setS(int S) {
		if (S > 0) {
			this.S = S;
		}
	}
//A method find1NN(LabelledPoint pt) that finds the nearest neighbor
// of a query point and return it as an instance of LabelledPoint.

	public LabelledPoint find1NN(LabelledPoint queryPoint,int K,int S) {
		// Initialize the array A to store nearest vectors
		
		ArrayList<LabelledPoint> A = new ArrayList<>(S);

		// uncheck all vertices
		for (LabelledPoint vertex : annGraph.getVertices()) {
			vertex.unchecked();
		}
		// Step 1: Randomly select a vertex W in the graph
		LabelledPoint randomVertex = annGraph.getRandomVertex();

		// Step 2: Compute the distance between W and Q
		double distanceToQuery = queryPoint.distanceTo(randomVertex);
		randomVertex.setIKey(queryPoint.getLabel());
		randomVertex.setKey(distanceToQuery);


		// Step 3: Insert W into the sorted array A
		A.add(randomVertex);

		LabelledPoint currentVertex=randomVertex;

		// Step 6: Traverse the graph
		while (currentVertex != null) {
			
			currentVertex=getUncheckedVertex(A);
			

			// Step 5: Mark vertex C as checked

				// Step 5: Mark vertex C as checked
				if (currentVertex != null) {
					currentVertex.checked();

					// Step 6.a: For each vertex V adjacent to C
					for (LabelledPoint adjacentVertex : annGraph.getNeighbors(currentVertex)) {

						// Step 6.a.i: Compute the distance between V and Q , if not done yet
						if (adjacentVertex.getIKey() != queryPoint.getLabel()) {
							distanceToQuery = queryPoint.distanceTo(adjacentVertex);
							adjacentVertex.setKey(distanceToQuery);
							adjacentVertex.setIKey(queryPoint.getLabel());



						// Step 6.a.ii: Insert V into A
					
						if (A.size() < S) {
						
							A.add(adjacentVertex);
						
							A.sort(Comparator.comparingDouble(LabelledPoint::getKey));
						} else {
							
							if (adjacentVertex.getKey() < A.get(A.size() - 1).getKey()) { //if farther than the last element in the array A, then it is not inserted
								A.get(A.size() - 1).unchecked();//uncheck removed vertex
								A.set(A.size() - 1, adjacentVertex);
								A.sort(Comparator.comparingDouble(LabelledPoint::getKey));
							}
						}
						}

					}
				}
		}

		// Step 8: The first K vertices in A are the approximate K nearest neighbors
	

		List<LabelledPoint> kNearestNeighbors = A.subList(0, K);

		return kNearestNeighbors.get(0);
		
	}


	// Helper method to get the unchecked vertex in A with the smallest distance to Q
	private LabelledPoint getUncheckedVertex(ArrayList<LabelledPoint> A) {
		for (LabelledPoint vertex : A) {
			if (!vertex.isChecked()) {
				return vertex;
			}
		}
		
		return null; // Return null if no unchecked vertex is found
	}



	public int size() { return annGraph.size(); }

    public static void main(String[] args) {
		if (args.length != 4) {
			System.err.println("Usage: java GraphA1NN <k> <S> <dataset filename> <query point filename>");
			System.exit(1);
		}
		int k = Integer.parseInt(args[0]);
		int S = Integer.parseInt(args[1]);
		String datasetFilename = args[2];
		String queryFilename = args[3];
		if (k>S) {
			System.err.println("k should be inferior than S");
			System.exit(1);
		}
		GraphA1NN graph = new GraphA1NN(datasetFilename);

		//construct the graph
		graph.constructKNNGraph(k,"knn.txt");
	
        System.out.println("Number of vertices: "+ graph.size());

		// Read the query points from the query file
		PointSet querySet = new PointSet(PointSet.read_ANN_SIFT(queryFilename));

		long startTime = System.currentTimeMillis(); // start timer
		double avgtime=0.0;

		for (LabelledPoint queryPoint : querySet.getPointsList()) {
			long startTimePerQuery = System.currentTimeMillis();
			LabelledPoint nearestNeighbor = graph.find1NN(queryPoint, k, S);
			long endTimePerQuery = System.currentTimeMillis();
			avgtime+= (double)(endTimePerQuery - startTimePerQuery); // time to execute a query
			// Print the nearestNeighbor for each query point
			System.out.print(queryPoint.getLabel() + " : ");
			System.out.print(nearestNeighbor.getLabel());
			System.out.println();

		}
		// print the average time to execute a query
		avgtime/= (double)(querySet.getPointsList().size());
		System.out.println("Average execution time: " + avgtime + " milliseconds");

		// print the total time to execute all queries
		long endTime = System.currentTimeMillis(); // end timer
		System.out.println("Total Execution time: " + (endTime - startTime) + " milliseconds");

	}
}





