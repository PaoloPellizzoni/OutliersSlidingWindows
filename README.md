# OutliersSlidingWindows
A Java implementation of the experiments for the paper "k-Center Clustering with Outliers in Sliding Windows"



## Dataset generation
The original datasets, namely Higgs and Cover, are provided (compressed) in the data folder.
One can download and preprocess the datasets as follows:

    wget https://archive.ics.uci.edu/ml/machine-learning-databases/00280/HIGGS.csv.gz
    cat HIGGS.csv.gz | gunzip | cut -d ',' -f 23,24,25,26,27,28,29 > higgs.dat

    wget https://archive.ics.uci.edu/ml/machine-learning-databases/covtype/covtype.data.gz
    gunzip covtype.data.gz


The script datasets.sh decompresses the zipped original datasets and generates the
artificial datasets used in the paper.
In particular, the program InjectOutliers takes a dataset and injects artificial outliers.
It takes as an argument:
- in, the path to the input dataset
- out, the path to the output file
- p, the probability with which to inject an outlier after every point
- r, the scaling factor for the norm of the outlier points
- d, the dimension of the points

The program GenerateArtificial generates automatically a dataset with points in a unit
ball with outliers on the suface of a ball of radius r.
It takes as an argument:
- out, the path to the output file
- p, the probability with which to inject an outlier
- r, the radius of the outer ball
- d, the dimension of the points

## Running the experiments 
The script exec.sh runs a representative subset of the experiments presented in the paper.

The program Main runs the experiments on the comparison of our k-center
algorithm with the sequential ones. 
It takes as and argument:
- in, the path to the input dataset
- out, the path to the output file
- d, the dimension of the points
- k, the number of centers
- z, the number of outliers
- N, the window size
- beta, eps, lambda, parameters of our method
- minDist, maxDist, parameters of our method
- samp, the number of candidate centers for sampled-charikar
- doChar, if set to 1 executes charikar, else it is skipped


It outputs, in the folder out/k-cen/, a file with:
- the first line reporting the parameters of the experiments
- a line for each of the sampled windows reporting, for each of the four methods, the update times,
the query times, the memory usage and the clustering radius. 

The program MainLambda runs the experiments on the sensitivity on lambda.
It takes as and argument:
- in, the path to the input dataset
- out, the path to the output file
- d, the dimension of the points
- k, the number of centers
- z, the number of outliers
- N, the window size
- beta, eps, lambda, parameters of our method (lambda unused)
- minDist, maxDist, parameters of our method
- doSlow, if set to 1 executes the slowest test, else it is skipped


It outputs, in the folder out/lam/, a file with:
- the first line reporting the parameters of the experiments
- a line for each of the sampled windows reporting, for each of the four methods, the update times,
the query times, the memory usage due to histograms, the total memory usage and the clustering radius. 

The program MainEffDiam runs the experiments on the effective diameter algorithms.
It takes as and argument:
- in, the path to the input dataset
- out, the path to the output file
- d, the dimension of the points
- alpha, fraction fo distances to discard
- eta, lower bound on ratio between effective diameter and diameter
- N, the window size
- beta, eps, lambda, parameters of our method
- minDist, maxDist, parameters of our method
- doSeq, if set to 1 executes the sequential method, else it is skipped


It outputs, in the folder out/diam/, a file with:
- the first line reporting the parameters of the experiments
- a line for each of the sampled windows reporting, for each of the two methods, the update times,
the query times, the memory usage and the effective diameter estimate. 
