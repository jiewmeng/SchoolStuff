/**
 * CS3211 Assignment 3: MPI
 * Done by Lim Jiew Meng (A0087884H)
 * 
 * Finding distributed minimums. For example: 
 *    
 *     process #1 has : 5 8 6 1
 *     process #2 has : 7 5 1 6
 *     process #3 has : 2 4 3 8
 *     ------------------------
 *     overall mins   : 2 4 1 1
 */

#include "mpi.h"
#include <stdio.h>
#include <time.h>
#include <stdlib.h>
#include "string.h"

int main(int argc, char** argv) {
	int rank, size;
	int values[10], mins[10];
	MPI_Init(&argc, &argv);
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);
	MPI_Comm_size(MPI_COMM_WORLD, &size);

	// generate random values
	int i;
	srand(time(NULL) + rank);
	for (i = 0; i < 10; i++) {
		values[i] = rand() % 10;
	}
	// extra retarded way of outputting an array instead of sprintf 
	// and casting ints in pure C ... sorry not really good in C
	printf("#%d has values %d %d %d %d %d %d %d %d %d %d \n", rank, 
		values[0], values[1], values[2], values[3], values[4], 
		values[5], values[6], values[7], values[8], values[9]);
	MPI_Barrier(MPI_COMM_WORLD);

	// do min reduction
	MPI_Reduce(&values, &mins, 10, MPI_INT, MPI_MIN, 0, MPI_COMM_WORLD);

	if (rank == 0) {
		printf("Global mins : ");
		for (i = 0; i < 10; i++) {
			printf("%d ", mins[i]);
		}
		printf("\n");
	}

	MPI_Finalize();
}
