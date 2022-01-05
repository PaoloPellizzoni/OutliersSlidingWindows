#!/bin/bash
set -x

javac src/test/Main.java
javac src/test/MainLambda.java
javac src/test/MainEffDiam.java
#                      in       out        		       dim k z  wsiz beta eps lam  mD   MD   samp  doChar
# k-center tests
java src.test.Main higgs.dat higgs10k-10.dat		    7 10 10 10000 0.5 0.66  0.5 0.01 10000 1000 1
#java src.test.Main higgs+10k.dat higgs+10k-10.dat		7 10 10 10000 0.5 0.66  0.5 0.01 10000 1000 1
#java src.test.Main cover.dat cov10k-10.dat		   55 10 10 10000 0.5 0.66  0.5 0.01 10000 1000 1
#java src.test.Main cover+10k.dat cov+10k-10.dat	   55 10 10 10000 0.5 0.66  0.5 0.01 10000 1000 1
#java src.test.Main higgs.dat higgs100k-10.dat		    7 10 10 100000 0.5 0.66 0.5 0.01 10000 1000 0
#java src.test.Main higgs+100k.dat higgs+100k-10.dat	7 10 10 100000 0.5 0.66 0.5 0.01 10000 1000 0
# ...
#
# lambda tests
java src.test.MainLambda higgs.dat higgs10k-10.dat		    7 10 10 10000 0.5   0.66  0.5 0.01 10000 1
#java src.test.MainLambda cover.dat cov10k-10.dat	   55 10 10 10000 0.5   0.66  0.5 0.01 10000 1
# ...
#                      in       out        		    	  dim alp eta   wsiz beta eps lam  mD   MD  doSeq
# eff diam tests
java src.test.MainEffDiam higgs-eff.dat higgs10k.dat	    7 0.9 0.001 10000 0.5  1.66  0.5 0.01 10000 1
#java src.test.MainEffDiam higgs-eff.dat higgs100k.dat	    7 0.9 0.001 100000 0.5 1.66  0.5 0.01 10000 1
#java src.test.MainEffDiam higgs-eff.dat higgs1M.dat	    7 0.9 0.001 1000000 0.5 1.66 0.5 0.01 10000 0
# ...

$SHELL
