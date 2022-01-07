CREATE
DATABASE test_db ENGINE = Atomic COMMENT 'test clickhouse db';

CREATE TABLE IF NOT EXISTS test_db.test_table
(
    `c_int8`
    Int8,
    `c_int16`
    Int16,
    `c_int32`
    Int32,
    `c_int64`
    Int64,
    `c_int128`
    Int128,
    `c_int256`
    Int256,
    `c_float32`
    Float32,
    `c_float64`
    Float64,
    `c_decimal32_9`
    Decimal32
(
    9
),
    `c_decimal64_18` Decimal64
(
    18
),
    `c_decimal128_38` Decimal128
(
    38
),
    `c_decimal256_76` Decimal256
(
    76
),
    `c_string` String,
    `c_fixedstring` FixedString
(
    2
),
    `c_date` Date,
    `c_date32` Date32,
    `c_datetime` DateTime
(
    'Asia/Seoul'
),
    `c_datetime64` DateTime64
(
    3,
    'Asia/Seoul'
),
    `c_array_int8` Array
(
    Int8
),
    `c_array_int16` Array
(
    Int16
),
    `c_array_string` Array
(
    String
),
    `c_nested` Nested
(
    nested_c_int32
    Int32,
    nested_c_array_int16
    Array
(
    Int16
), nested_c_array_string Array
(
    String
)),
    `c_tuple` Tuple
(
    Nullable
(
    String
), Nullable
(
    String
))
    )
    ENGINE = MergeTree
(
)
    PRIMARY KEY c_int8;

INSERT INTO test_db.test_table (c_int8, c_int16, c_int32, c_int64, c_int128, c_int256, c_float32, c_float64,
                                c_decimal32_9, c_decimal64_18, c_decimal128_38, c_decimal256_76, c_string,
                                c_fixedstring, c_date, c_date32, c_datetime, c_datetime64, c_array_int8, c_array_int16,
                                c_array_string, `c_nested.nested_c_int32`, `c_nested.nested_c_array_int16`,
                                `c_nested.nested_c_array_string`, c_tuple)
VALUES (1, 32767, 2147483647, 9223372036854775807, 170141183460469231731687303715884105727,
        57896044618658097711785492504343953926634992332820282019728792003956564819967, 0.09999999999999998,
        0.09999999999999998, 0.9999, 0.999999999, 0.99999999999999, 0.99999999999999999999, 'string test', 'fs',
        1546300800, 4102444800, 1546300800, 1546300800000, array(1, 2, 3), array(100, 1000, 10000),
        array('test', 'string', 'array'), null, null, null, tuple('key1', 'value1'));