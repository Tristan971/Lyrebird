#!/usr/bin/env bash

# Calculate the day number using the date command:
IS5DAYS=$[ $(date +"%-d") % 5 ]

# Every five days, update dependencies
if [ ${IS5DAYS} -eq "0" ]; then
  mvn dependency:update-snapshots
fi
