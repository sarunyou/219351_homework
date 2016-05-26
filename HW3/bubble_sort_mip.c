#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define N 100000
#define send_data_tag 2001
#define return_data_tag 2002
void swap(int *xp, int *yp) {
        int temp = *xp;
        *xp = *yp;
        *yp = temp;
}

// A function to implement bubble sort
void bubbleSort(int arr[],int n) {
        int i, j;
        for (i = 0; i < n-1; i++)
                for (j = 0; j < n-i-1; j++)
                        if (arr[j] > arr[j+1])
                                swap(&arr[j], &arr[j+1]);
}


int isSorted(int *a, int size) {
        int i;
        for (i = 0; i < size - 1; i++) {
                if (a[i] > a[i + 1]) {
                        return 0;
                }
        }
        return 1;
}

// Function to print an array
void printArray(int arr[], int size)
{
        int i;
        for (i=0; i < size; i++)
                printf("%d ", arr[i]);
        printf("\n");
}
void printAllArray(int arr[],int size){
        int i;
        for (i = 0; i < size; i++) {
                printf("%d ", arr[i]);
        }
        printf("\n" );


}

int main(int argc, char** argv) {
        int i, n;
        int* A;
        int* B;
        int* C;
        clock_t start, end;
        double elapsed_time, t1, t2;
        MPI_Status status;
        int start_row,an_id,end_row,num_rows_to_send,num_rows_to_receive,num_rows_received,start_row_return;
        MPI_Init(NULL, NULL);

        // Find out rank, size
        int world_rank;
        MPI_Comm_rank(MPI_COMM_WORLD, &world_rank);
        int world_size;
        MPI_Comm_size(MPI_COMM_WORLD, &world_size);

        t1 = MPI_Wtime();
        A = (int *)malloc(sizeof(int)*N);

        if (A == NULL) {
                printf("Fail to malloc\n");
                exit(0);
        }

        int root_process = 0;
        int avg_rows_per_process = N / world_size;
        B = (int *)malloc(sizeof(int)*avg_rows_per_process);
        C = (int *)malloc(sizeof(int)*avg_rows_per_process);
        if(world_rank == root_process) {
                //I must be the root process
                //Initialize an  array
                for (i=N-1; i>=0; i--)
                        A[N-1-i] = i;
                for (an_id = 1; an_id < world_size; an_id++) {
                        /* code */
                        start_row = an_id*avg_rows_per_process + 1;
                        end_row = (an_id + 1)*avg_rows_per_process;

                        if((N - end_row) < avg_rows_per_process)
                                end_row = N - 1;
                        num_rows_to_send = end_row - start_row + 1;
                        // printf("start_row is %i to end_row is %i\n",start_row,end_row );
                        MPI_Send( &start_row, 1, MPI_INT,
                                  an_id, send_data_tag, MPI_COMM_WORLD);
                        MPI_Send( &A[start_row-1], avg_rows_per_process, MPI_INT,
                                  an_id, send_data_tag, MPI_COMM_WORLD);
                }

                //sort by root process
                bubbleSort(A,avg_rows_per_process);
                printf("array from %d to %d sorted by root process\n",0,start_row-1 );
                // printAllArray(A,avg_rows_per_process);
                for (an_id = 1; an_id < world_size; an_id++) {
                        MPI_Recv( &start_row_return, 1, MPI_INT,
                                  MPI_ANY_SOURCE, return_data_tag, MPI_COMM_WORLD,MPI_STATUS_IGNORE);
                        MPI_Recv( C,avg_rows_per_process, MPI_INT,
                                    MPI_ANY_SOURCE,return_data_tag, MPI_COMM_WORLD,&status);

                        int sender = status.MPI_SOURCE;
                        printf("array from %d to %d sorted by slave process %d\n",start_row_return,start_row_return+avg_rows_per_process-1,sender );
                        // printAllArray(C,avg_rows_per_process);
                        int pivot = start_row_return;
                        int i ;
                        for (i = 0; i < avg_rows_per_process; i++) {
                            A[pivot] = C[i];
                            pivot++;
                        }
                        // printAllArray(A,N);
                }
                // finally, sorted angain after some portion of array sorted
                printf("array A  before  last sort\n" );
                // bubbleSort(A,N);
                // printf("result of Arry A is \n" );
                t2 = MPI_Wtime();
                printf( "Elapsed time MPI_Wtime is %f\n", t2 - t1 );
                // if (isSorted(A, N))
                // printf("Array is sorted\n");
                // else
                // printf("Array is NOT sorted\n");
        } else {
                //I must be the slave process
                MPI_Recv( &start_row, 1, MPI_INT,
                          root_process, send_data_tag, MPI_COMM_WORLD,MPI_STATUS_IGNORE);
                MPI_Recv( B,avg_rows_per_process, MPI_INT,
                          root_process, send_data_tag, MPI_COMM_WORLD,MPI_STATUS_IGNORE);
                // printArray(&B[avg_rows_per_process-20], 20);
                bubbleSort(B,avg_rows_per_process);
                // printf("array B is\n" );
                // printAllArray(B,avg_rows_per_process);

                MPI_Send( &start_row, 1, MPI_INT,
                          root_process, return_data_tag, MPI_COMM_WORLD);
                MPI_Send( &B[0], avg_rows_per_process, MPI_INT,
                          root_process, return_data_tag, MPI_COMM_WORLD);


        }

        MPI_Finalize();
        free(A);
        free(B);
        free(C);
        return 0;
}
