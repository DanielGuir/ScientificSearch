#!/bin/bash
#SBATCH --job-name=wikipedia_index
#SBATCH -x linux[41-60],gpu-compute[1-7]
#SBATCH -p compsci --mem=64gb
#SBATCH --output index.out
#SBATCH --error index.err

sbt "extra/runMain ai.lum.odinson.extra.IndexDocuments"

(?<node2>[norm=Anderson]) >conj_and (?<node4>[]) >nsubj (?<node0>[])