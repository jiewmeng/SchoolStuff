/**
 * CS3211 Assignment 3: MPI
 * Done by Lim Jiew Meng (A0087884H)
 * 
 * Computing prefix sum (see figure 2 of http://cva.stanford.edu/classes/cs99s/papers/hillis-steele-data-parallel-algorithms.pdf)
 */

#include "mpi.h"
#include <stdio.h>
#include <time.h>
#include <stdlib.h>
#include "math.h"

int main(int argc, char** argv) {
	int rank, size, value, sum;
	MPI_Init(&argc, &argv);
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);
	MPI_Comm_size(MPI_COMM_WORLD, &size);
	int values[size];
	
	// Root (0) generates random values
	if (rank == 0) {
		int i;
		srand(time(NULL) + rank);
		printf("Generated values: ");
		for (i = 0; i < size; i++) {
			values[i] = rand() % 10;
			printf("%d ", values[i]);
		}
		printf("\n");
	}

	// then scatters to other processes
	MPI_Scatter(&values, 1, MPI_INT, 
		&value, 1, MPI_INT, 
		0, MPI_COMM_WORLD);

	// do prefix sum (note: MPI_Scan is a faster way)
	int level, tmp, prevRank;
	for (level = 1; level <= ceil(log2(size)); level++) {
		if (rank >= (int) pow(2, level-1)) {
			MPI_Recv(&tmp, 1, MPI_INT, rank - (int)pow(2, level-1), 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		}
		if (rank < (size - (int) pow(2, level-1))) {
			MPI_Send(&value, 1, MPI_INT, rank + (int)pow(2, level-1), 0, MPI_COMM_WORLD);
		}

		MPI_Barrier(MPI_COMM_WORLD);

		if (rank >= (int) pow(2, level-1)) {
			value = value + tmp;
		}
	}

	// print results
	printf("Prefix sum at #%d is %d \n", rank, value);

	MPI_Finalize();
}
