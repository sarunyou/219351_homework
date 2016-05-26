items = LOAD './test.txt' USING PigStorage(' ') AS (fromNode: int, toNode: int);
fromTo = GROUP items by fromNode;
fromTo = FOREACH fromTo GENERATE items.fromNode AS k, items.toNode AS v, 'fromTo' AS type;
toFrom = GROUP items by toNode;
toFrom = FOREACH toFrom GENERATE items.toNode AS k, items.fromNode AS v, 'toFrom' AS type;

relations = UNION fromTo, toFrom;
relations = FOREACH relations GENERATE FLATTEN($0) AS k, $1 AS v, $2 AS type;

relations = GROUP relations by k;
DUMP relations;
-- DUMP fromTo;



-- results = FOREACH grouped GENERATE group, ;
-- DUMP results;
