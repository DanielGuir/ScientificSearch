#!/bin/bash

# Define variables
partition="compsci"

for i in {0..350}
do
# remove the sbatch file
rm run.sbatch
# Create the sbatch file
cat << EOF > run.sbatch
#!/bin/bash
#SBATCH --job-name=${i}
#SBATCH -x linux[41-60],gpu-compute[1-7]
#SBATCH -p ${partition} --mem=8gb
#SBATCH --output sbatch_outputs/$i.out
#SBATCH --error sbatch_outputs/$i.err

sbt "extra/runMain ai.lum.odinson.extra.AnnotateText /usr/xtmp/rg315/generated_20181220_10000/$i/ /usr/xtmp/rg315/annotated_documents/ CluProcessor"
EOF

# Submit the job
sbatch run.sbatch
done
