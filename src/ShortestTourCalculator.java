
/** 
 * Given n destinations and the distance between each pair of 
 * destinations, ShortestTourCalculator uses a branch and 
 * bound recursive tree search to determine the shortest tour to visit 
 * each destination exactly once and return back to the starting 
 * destination.
 * 
 * This algorithm was published in the book Discrete Mathematics and 
 * Its Applications: A Java Library of Graph Algorithms and Optimization 
 * by Kenneth H. Rosen. 
 * 
 * @author Kenneth H. Rosen
 */
public class ShortestTourCalculator {
	/**
	 * Given n destinations and the distance between each pair of 
	 * destinations, the travelingSalesmanProblem uses a branch and 
	 * bound recursive tree search to determine the shortest tour to visit 
	 * each destination exactly once and return back to the starting 
	 * destination.
	 * 
	 * @param n		Number of destinations
	 * @param dist	Distance between each pair of destinations 
	 * @param sol	The minimum cost cycle is given by: 
	 * 				sol[1], sol[2], ..., sol[n]
	 */
	public static void travelingSalesmanProblem(
			int n, double dist[][], int sol[]) {
		int i, p;
		int row[] = new int[n + 1];
		int column[] = new int[n + 1];
		int front[] = new int[n + 1];
		int cursol[] = new int[n + 1];
		int back[] = new int[n + 1];

		for (i = 1; i <= n; i++) {
			row[i] = i;
			column[i] = i;
			front[i] = 0;
			back[i] = 0;
		}

		dist[0][0] = Double.MAX_VALUE;
		tspsearch(n, 0, 0, dist, row, column, cursol, front, back);
		p = 1;

		for (i = 1; i <= n; i++) {
			sol[i] = p;
			p = cursol[p];
		}
	}

	/**
	 * This method is used internally by the method travelingSalesmanProblem. 
	 * 
	 * It takes into account the distances between each pair of destinations 
	 * and determines the shortest tour.
	 * 
	 * @param nodes		Destinations 
	 * @param edges		Path from one destination to the next
	 * @param weight	Weight of the distances between each 
	 * 					pair of destinations 
	 * @param dist		Distance between each pair of destinations  
	 * @param row		Row of the 2D distance array
	 * @param column	Column of the 2D distance array
	 * @param cursol	An object that holds the current possible solution/path
	 * @param front		First element of the solution array
	 * @param back		Last element of the solution array
	 */
	static private void tspsearch(int nodes, int edges, int weight,
			double dist[][], int row[], int column[],
			int cursol[], int front[], int back[])
	{
		int i, j, k, reduction, skip, candc = 0, candr = 0;
		double small, stretch; 
		int elms, head, tail, blank;
		double minx, miny, diff, thresh; 
		double cutx[] = new double[nodes + 1];
		double cuty[] = new double[nodes + 1];
		int rowvec[] = new int[nodes + 1];
		int colvec[] = new int[nodes + 1];

		elms = nodes - edges;
		reduction = 0;

		for (i = 1; i <= elms; i++) {
			small = Integer.MAX_VALUE;

			for (j = 1; j <= elms; j++) {
				small = Math.min(small, dist[row[i]][column[j]]);
			}

			if (small > 0) {
				for (j = 1; j <= elms; j++)
					if (dist[row[i]][column[j]] < Integer.MAX_VALUE)
						dist[row[i]][column[j]] -= small;
				reduction += small;
			}

			cutx[i] = small;
		}

		for (j = 1; j <= elms; j++) {
			small = Double.MAX_VALUE;

			for (i = 1; i <= elms; i++) {
				small = Math.min(small, dist[row[i]][column[j]]);
			}

			if (small > 0) {
				for (i = 1; i <= elms; i++)
					if (dist[row[i]][column[j]] < Integer.MAX_VALUE)
						dist[row[i]][column[j]] -= small;
				reduction += small;
			}

			cuty[j] = small;
		}

		weight += reduction;

		if (weight < dist[0][0]) {
			if (edges == (nodes - 2)) {
				for (i = 1; i <= nodes; i++)
					cursol[i] = front[i];
				skip = (dist[row[1]][column[1]] == Integer.MAX_VALUE ? 1 : 2);
				cursol[row[1]] = column[3-skip];
				cursol[row[2]] = column[skip];
				dist[0][0] = weight;

			} else {
				diff = -Integer.MAX_VALUE;
				for (i = 1; i <= elms; i++)
					for (j = 1; j <= elms; j++)
						if (dist[row[i]][column[j]] == 0) {

							minx = Double.MAX_VALUE;
							blank = 0;

							for (k = 1; k <= elms; k++)
								if (dist[row[i]][column[k]] == 0)
									blank++;
								else
									minx = Math.min(
											minx, dist[row[i]][column[k]]);
							if (blank > 1) minx = 0;

							miny = Integer.MAX_VALUE;
							blank = 0;

							for (k = 1; k <= elms; k++)
								if (dist[row[k]][column[j]] == 0)
									blank++;
								else
									miny = Math.min(
											miny, dist[row[k]][column[j]]);
							if (blank > 1) miny = 0;
							if ((minx + miny) > diff) {
								diff = minx + miny;
								candr = i;
								candc = j;
							}
						}

				thresh = weight + diff;
				front[row[candr]] = column[candc];
				back[column[candc]] = row[candr];

				tail = column[candc];

				while (front[tail] != 0)
					tail = front[tail];

				head = row[candr];

				while (back[head] != 0)
					head = back[head];

				stretch = dist[tail][head];
				dist[tail][head] = Integer.MAX_VALUE;

				for (i = 1; i <= candr - 1; i++)
					rowvec[i] = row[i];
				for (i = candr; i <= elms - 1; i++)
					rowvec[i] = row[i+1];
				for (i = 1; i <= candc - 1; i++)
					colvec[i] = column[i];
				for (i = candc; i <= elms - 1; i++)
					colvec[i] = column[i+1];

				tspsearch(nodes, edges + 1, 
						weight, dist, rowvec, colvec, cursol, front, back);
				dist[tail][head] = stretch;
				back[column[candc]] = 0;
				front[row[candr]] = 0;

				if (thresh < dist[0][0]) {
					dist[row[candr]][column[candc]] = Integer.MAX_VALUE;
					tspsearch(
							nodes, edges, weight, dist, row, column,
							cursol, front, back);
					dist[row[candr]][column[candc]] = 0;
				}
			}
		}

		for (i = 1; i <= elms; i++)
			for (j = 1; j <= elms; j++)
				dist[row[i]][column[j]] += (cutx[i] + cuty[j]);
	}
}
