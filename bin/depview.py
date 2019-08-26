#!/usr/bin/env python3

import os
import argparse
from pathlib import Path
from subprocess import call

def depview(args):

    if not Path(args.conf).exists():
        raise RuntimeError("No such file: {}".format(args.conf))

    projectPath = Path(os.getcwd())
    targetPath = projectPath / 'target'
    if not targetPath.exists():
        raise RuntimeError("No such directory: {}. Please run `mvn package' first".format(str(targetPath)))

    fs = [f for f in targetPath.iterdir() if f.is_file() and f.name.endswith('.jar')]
    if len(fs) == 0:
        raise RuntimeError("No jars found in {}. Please run `mvn package' first".format(str(targetPath)))

    jar = fs[0]
    cmd = ['java', '-jar', str(jar), args.conf]
    print(' '.join(cmd))
    call(cmd)

if __name__ == '__main__':

    parser = argparse.ArgumentParser()
    parser.add_argument('conf')

    args = parser.parse_args()
    depview(args)
