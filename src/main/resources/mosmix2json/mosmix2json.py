#!/usr/bin/python3
# coding=UTF-8

import json, csv, argparse
from datetime import datetime
from pytz import timezone, utc

parser = argparse.ArgumentParser()
parser.add_argument("mosmix", type=str, help="CSV file with the MOSMIX data")
args = parser.parse_args()

gz = timezone('Europe/Berlin')
weekdays = {0: 'Montag', 1: 'Dienstag', 2: 'Mittwoch', 3: 'Donnerstag', 4: 'Freitag', 5: 'Samstag', 6: 'Sonntag'}
data = {
    'forecast_step_year': [],
    'forecast_step_month': [],
    'forecast_step_day': [],
    'forecast_step_hour': [],
    'forecast_step_dow': [],
    'forecast_step_tz': []
}

# Parse mosmix CSV
with open(args.mosmix, encoding='UTF-8') as mosmixFile:
    element_names = mosmixFile.readline().split(';')
    element_units = mosmixFile.readline().split(';')
    for element_index in range(2, len(element_names)):
        element_name = element_names[element_index].strip()
        element_unit = element_units[element_index].strip()
        data[element_name] = []
        data[element_name + '_unit'] = element_unit
    modelrun = element_units[0][6:12]
    mosmixReader = csv.reader(mosmixFile, delimiter=';')
    for row in mosmixReader:
        forecast_date = row[0]
        forecast_time = row[1]
        forecast_datetime_utc = datetime(int("20" + forecast_date[6:8]),
                                         int(forecast_date[3:5]),
                                         int(forecast_date[0:2]),
                                         int(forecast_time[0:2]),
                                         int(forecast_time[3:5]),
                                         0,
                                         0,
                                         utc)
        forecast_datetime_gz = forecast_datetime_utc.astimezone(gz)
        data['forecast_step_year'].append(forecast_datetime_gz.year)
        data['forecast_step_month'].append('{0:0>2}'.format(forecast_datetime_gz.month))
        data['forecast_step_day'].append('{0:0>2}'.format(forecast_datetime_gz.day))
        data['forecast_step_hour'].append('{0:0>2}'.format(forecast_datetime_gz.hour))
        data['forecast_step_dow'].append(weekdays[forecast_datetime_gz.weekday()])
        data['forecast_step_tz'].append(forecast_datetime_gz.tzname())
        for element_index in range(2, len(row)):
            element_name = element_names[element_index].strip()
            element_values = data[element_name]
            value = row[element_index].strip()
            element_values.append(value)

# WWN
with open('ww.json', encoding='UTF-8') as file:
    WW = json.load(file)
with open('n.json', encoding='UTF-8') as file:
    N = json.load(file)

wwn = []
step = 0
for value in data['ww']:
    if value in WW:
        ww = WW[value]
        wwn.append(ww)
    else:
        n = data['Nf'][step]
        if n in N:
            n = N[n]
        wwn.append(n)
    step = step + 1
data['wwn'] = wwn

wwn3 = []
step = 0
for value in data['ww3']:
    if value in WW:
        ww = WW[value]
        wwn3.append(ww)
    else:
        n = data['Nf'][step]
        if n in N:
            n = N[n]
        wwn3.append(n)
    step = step + 1
data['wwn3'] = wwn3

# dd
ddd = []
for value in data['dd']:
    if value == '---':
        dd = value
    else:
        value = float(value)
        if 337.5 < value <= 360:
            dd = 'N'
        elif 0 <= value <= 22.5:
            dd = 'N'
        elif 22.5 < value <= 67.5:
            dd = 'NO'
        elif 67.5 < value <= 112.5:
            dd = 'O'
        elif 112.5 < value <= 157.5:
            dd = 'SO'
        elif 157.5 < value <= 202.5:
            dd = 'S'
        elif 202.5 < value <= 247.5:
            dd = 'SW'
        elif 247.5 < value <= 292.5:
            dd = 'W'
        elif 292.5 < value <= 337.5:
            dd = 'NW'
        else:
            dd = 'ERR'
    ddd.append(dd)
data['ddd'] = ddd

# Rounding
for element in ['TT', 'ff', 'fx3', 'PPPP', 'Td', 'Tx', 'Tn', 'Tg', 'Tm']:
    values = data[element]
    values_rounded = []
    for value in values:
        if value != '---':
            value = int(round(float(value) + 0))
        values_rounded.append(value)
    data[element] = values_rounded
    data[element + '_org'] = values

# fx
fx = []
for value in data['fx3']:
    if value != '---' and int(value) >= 40:
        fx.append("<font color='red'><b>{:d}</b></font>".format(value))
    else:
        fx.append('---')
data['fx3_html'] = fx

fx = []
for value in data['fx']:
     if value != '---' and float(value) >= 40:
         fx.append("<font color='red'><b>{:.0f}</b></font>".format(float(value)))
     else:
         fx.append('---')
data['fx1_html'] = fx

# Modelrun
data['modelrun'] = modelrun

print(json.dumps(data, sort_keys=True, indent=True))
