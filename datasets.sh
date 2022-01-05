#!/bin/bash 
set -x

cat data/higgs.zipaa data/higgs.zipab data/higgs.zipac data/higgs.zipad > data/higgs_full.zip 
unzip data/higgs_full.zip -d data/
unzip data/cover.zip -d data/

javac src/core/utils/InjectOutliers.java
javac src/core/utils/GenerateArtificial.java

# create Higgs+ datasets
java src.core.utils.InjectOutliers data/higgs.dat data/higgs+10k.dat   0.0005 100 7
java src.core.utils.InjectOutliers data/higgs.dat data/higgs+100k.dat 0.00005 100 7
java src.core.utils.InjectOutliers data/higgs.dat data/higgs+1M.dat  0.000005 100 7

# create Cover+ datasets 
java src.core.utils.InjectOutliers data/cover.dat data/cover+10k.dat   0.0005 100 55
java src.core.utils.InjectOutliers data/cover.dat data/cover+100k.dat 0.00005 100 55

# create Artificial datasets
java src.core.utils.GenerateArtificial data/artif10.dat   0.001 10 4
java src.core.utils.GenerateArtificial data/artif100.dat  0.001 100 4
java src.core.utils.GenerateArtificial data/artif1000.dat 0.001 1000 4

# create dataset for effective diameter 
java src.core.utils.InjectOutliers data/higgs.dat data/higgs-eff.dat   0.001 100 7

$SHELL