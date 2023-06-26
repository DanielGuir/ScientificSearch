#!/bin/bash
#SBATCH --job-name=retrieval
#SBATCH -x linux[41-60],gpu-compute[1-7]
#SBATCH -p compsci --mem=32gb
#SBATCH --output retrieve.out
#SBATCH --error retrieve.err

sbt "extra/runMain export.GetAllMatches"