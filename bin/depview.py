#!/usr/bin/env python3

import os
import argparse
from pathlib import Path
from subprocess import call

def depview(args):

    if not Path(args.conf).exists():
        raise RuntimeError("No such file: {}".format(args.conf))

    cmd = ['mvn', 'spring-boot:run', '-Dspring-boot.run.arguments={}'.format(args.conf)]
    print(' '.join(cmd))
    call(cmd)

if __name__ == '__main__':

    parser = argparse.ArgumentParser()
    parser.add_argument('conf')

    args = parser.parse_args()
    depview(args)
