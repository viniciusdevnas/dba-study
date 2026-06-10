--
-- PostgreSQL database dump
--

\restrict 8wIYYHjc2NTvcux1cozdIhWNycfBRgl3hReHMZvbxhuKbfpVxbcCc1fgxWJ7BKr

-- Dumped from database version 18.2
-- Dumped by pg_dump version 18.2

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: sgv_schema; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA sgv_schema;


ALTER SCHEMA sgv_schema OWNER TO postgres;

--
-- Name: level; Type: TYPE; Schema: sgv_schema; Owner: postgres
--

CREATE TYPE sgv_schema.level AS ENUM (
    'SELLER',
    'MANAGER'
);


ALTER TYPE sgv_schema.level OWNER TO postgres;

--
-- Name: region; Type: TYPE; Schema: sgv_schema; Owner: postgres
--

CREATE TYPE sgv_schema.region AS ENUM (
    'NORTH',
    'SOUTH',
    'EAST',
    'WEST'
);


ALTER TYPE sgv_schema.region OWNER TO postgres;

--
-- Name: status_requested; Type: TYPE; Schema: sgv_schema; Owner: postgres
--

CREATE TYPE sgv_schema.status_requested AS ENUM (
    'DRAFT',
    'PROCESSED',
    'CANCELED'
);


ALTER TYPE sgv_schema.status_requested OWNER TO postgres;

--
-- Name: unite_measure; Type: TYPE; Schema: sgv_schema; Owner: postgres
--

CREATE TYPE sgv_schema.unite_measure AS ENUM (
    'KG',
    'UN'
);


ALTER TYPE sgv_schema.unite_measure OWNER TO postgres;

--
-- Name: fn_random_sku(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_random_sku() RETURNS integer
    LANGUAGE plpgsql
    AS $$
    DECLARE
        v_sku_generate INT;
    BEGIN
        SELECT floor(random() * (9999999 - 1000 + 1) + 1000)::int INTO v_sku_generate;
        RETURN v_sku_generate;
    END;
$$;


ALTER FUNCTION public.fn_random_sku() OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: client; Type: TABLE; Schema: sgv_schema; Owner: postgres
--

CREATE TABLE sgv_schema.client (
    id_client bigint NOT NULL,
    name_client character varying(100) NOT NULL,
    birth_date date NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    is_active boolean DEFAULT true,
    cell_phone character varying(15) NOT NULL
);


ALTER TABLE sgv_schema.client OWNER TO postgres;

--
-- Name: client_id_client_seq; Type: SEQUENCE; Schema: sgv_schema; Owner: postgres
--

CREATE SEQUENCE sgv_schema.client_id_client_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE sgv_schema.client_id_client_seq OWNER TO postgres;

--
-- Name: client_id_client_seq; Type: SEQUENCE OWNED BY; Schema: sgv_schema; Owner: postgres
--

ALTER SEQUENCE sgv_schema.client_id_client_seq OWNED BY sgv_schema.client.id_client;


--
-- Name: item_requested; Type: TABLE; Schema: sgv_schema; Owner: postgres
--

CREATE TABLE sgv_schema.item_requested (
    id_item bigint NOT NULL,
    id_requested bigint NOT NULL,
    id_product bigint NOT NULL,
    quantity numeric(15,2) NOT NULL,
    unit_measure sgv_schema.unite_measure NOT NULL,
    price numeric(15,2) NOT NULL,
    date timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE sgv_schema.item_requested OWNER TO postgres;

--
-- Name: item_requested_id_item_seq; Type: SEQUENCE; Schema: sgv_schema; Owner: postgres
--

CREATE SEQUENCE sgv_schema.item_requested_id_item_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE sgv_schema.item_requested_id_item_seq OWNER TO postgres;

--
-- Name: item_requested_id_item_seq; Type: SEQUENCE OWNED BY; Schema: sgv_schema; Owner: postgres
--

ALTER SEQUENCE sgv_schema.item_requested_id_item_seq OWNED BY sgv_schema.item_requested.id_item;


--
-- Name: manifacturer; Type: TABLE; Schema: sgv_schema; Owner: postgres
--

CREATE TABLE sgv_schema.manifacturer (
    id_manifacturer bigint NOT NULL,
    cnpj_manifacture character(15) NOT NULL,
    name_manifacture character varying(200) NOT NULL,
    cell_phone character varying(15) NOT NULL,
    is_active boolean DEFAULT true,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE sgv_schema.manifacturer OWNER TO postgres;

--
-- Name: manifacturer_id_manifacturer_seq; Type: SEQUENCE; Schema: sgv_schema; Owner: postgres
--

CREATE SEQUENCE sgv_schema.manifacturer_id_manifacturer_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE sgv_schema.manifacturer_id_manifacturer_seq OWNER TO postgres;

--
-- Name: manifacturer_id_manifacturer_seq; Type: SEQUENCE OWNED BY; Schema: sgv_schema; Owner: postgres
--

ALTER SEQUENCE sgv_schema.manifacturer_id_manifacturer_seq OWNED BY sgv_schema.manifacturer.id_manifacturer;


--
-- Name: office; Type: TABLE; Schema: sgv_schema; Owner: postgres
--

CREATE TABLE sgv_schema.office (
    id_office bigint NOT NULL,
    city_office character varying(255) NOT NULL,
    region sgv_schema.region NOT NULL,
    id_manager_office bigint NOT NULL
);


ALTER TABLE sgv_schema.office OWNER TO postgres;

--
-- Name: office_id_office_seq; Type: SEQUENCE; Schema: sgv_schema; Owner: postgres
--

CREATE SEQUENCE sgv_schema.office_id_office_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE sgv_schema.office_id_office_seq OWNER TO postgres;

--
-- Name: office_id_office_seq; Type: SEQUENCE OWNED BY; Schema: sgv_schema; Owner: postgres
--

ALTER SEQUENCE sgv_schema.office_id_office_seq OWNED BY sgv_schema.office.id_office;


--
-- Name: product; Type: TABLE; Schema: sgv_schema; Owner: postgres
--

CREATE TABLE sgv_schema.product (
    id_product bigint NOT NULL,
    name_product character varying(100) NOT NULL,
    description text,
    is_active boolean DEFAULT true,
    price_product numeric(15,2) NOT NULL,
    sku integer DEFAULT public.fn_random_sku(),
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE sgv_schema.product OWNER TO postgres;

--
-- Name: product_entry; Type: TABLE; Schema: sgv_schema; Owner: postgres
--

CREATE TABLE sgv_schema.product_entry (
    id_entry bigint NOT NULL,
    id_product bigint NOT NULL,
    id_office bigint NOT NULL,
    id_seller_responsable bigint NOT NULL,
    id_manifacturer bigint NOT NULL,
    quantity numeric(15,2) NOT NULL,
    unit_measure sgv_schema.unite_measure NOT NULL,
    date_entry timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE sgv_schema.product_entry OWNER TO postgres;

--
-- Name: product_entry_id_entry_seq; Type: SEQUENCE; Schema: sgv_schema; Owner: postgres
--

CREATE SEQUENCE sgv_schema.product_entry_id_entry_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE sgv_schema.product_entry_id_entry_seq OWNER TO postgres;

--
-- Name: product_entry_id_entry_seq; Type: SEQUENCE OWNED BY; Schema: sgv_schema; Owner: postgres
--

ALTER SEQUENCE sgv_schema.product_entry_id_entry_seq OWNED BY sgv_schema.product_entry.id_entry;


--
-- Name: product_id_product_seq; Type: SEQUENCE; Schema: sgv_schema; Owner: postgres
--

CREATE SEQUENCE sgv_schema.product_id_product_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE sgv_schema.product_id_product_seq OWNER TO postgres;

--
-- Name: product_id_product_seq; Type: SEQUENCE OWNED BY; Schema: sgv_schema; Owner: postgres
--

ALTER SEQUENCE sgv_schema.product_id_product_seq OWNED BY sgv_schema.product.id_product;


--
-- Name: requested; Type: TABLE; Schema: sgv_schema; Owner: postgres
--

CREATE TABLE sgv_schema.requested (
    id_requested bigint NOT NULL,
    id_client bigint NOT NULL,
    id_office bigint NOT NULL,
    status_requested sgv_schema.status_requested NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    update_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE sgv_schema.requested OWNER TO postgres;

--
-- Name: requested_id_requested_seq; Type: SEQUENCE; Schema: sgv_schema; Owner: postgres
--

CREATE SEQUENCE sgv_schema.requested_id_requested_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE sgv_schema.requested_id_requested_seq OWNER TO postgres;

--
-- Name: requested_id_requested_seq; Type: SEQUENCE OWNED BY; Schema: sgv_schema; Owner: postgres
--

ALTER SEQUENCE sgv_schema.requested_id_requested_seq OWNED BY sgv_schema.requested.id_requested;


--
-- Name: sale; Type: TABLE; Schema: sgv_schema; Owner: postgres
--

CREATE TABLE sgv_schema.sale (
    id_sale bigint NOT NULL,
    id_client bigint NOT NULL,
    id_seller bigint NOT NULL,
    id_requested bigint NOT NULL,
    id_office bigint NOT NULL,
    amount_total numeric(15,2) NOT NULL,
    date_sale timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE sgv_schema.sale OWNER TO postgres;

--
-- Name: sale_id_sale_seq; Type: SEQUENCE; Schema: sgv_schema; Owner: postgres
--

CREATE SEQUENCE sgv_schema.sale_id_sale_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE sgv_schema.sale_id_sale_seq OWNER TO postgres;

--
-- Name: sale_id_sale_seq; Type: SEQUENCE OWNED BY; Schema: sgv_schema; Owner: postgres
--

ALTER SEQUENCE sgv_schema.sale_id_sale_seq OWNED BY sgv_schema.sale.id_sale;


--
-- Name: sales_target; Type: TABLE; Schema: sgv_schema; Owner: postgres
--

CREATE TABLE sgv_schema.sales_target (
    id_sales_target bigint NOT NULL,
    id_office bigint NOT NULL,
    sales_target numeric(18,2) NOT NULL,
    start_date date NOT NULL,
    end_date date NOT NULL
);


ALTER TABLE sgv_schema.sales_target OWNER TO postgres;

--
-- Name: sales_target_id_sales_target_seq; Type: SEQUENCE; Schema: sgv_schema; Owner: postgres
--

CREATE SEQUENCE sgv_schema.sales_target_id_sales_target_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE sgv_schema.sales_target_id_sales_target_seq OWNER TO postgres;

--
-- Name: sales_target_id_sales_target_seq; Type: SEQUENCE OWNED BY; Schema: sgv_schema; Owner: postgres
--

ALTER SEQUENCE sgv_schema.sales_target_id_sales_target_seq OWNED BY sgv_schema.sales_target.id_sales_target;


--
-- Name: seller; Type: TABLE; Schema: sgv_schema; Owner: postgres
--

CREATE TABLE sgv_schema.seller (
    id_seller bigint NOT NULL,
    id_office bigint,
    name_seller character varying(255) NOT NULL,
    date_birth date NOT NULL,
    hiring_date date NOT NULL,
    level_seller sgv_schema.level NOT NULL,
    create_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    is_active boolean DEFAULT true
);


ALTER TABLE sgv_schema.seller OWNER TO postgres;

--
-- Name: seller_id_seller_seq; Type: SEQUENCE; Schema: sgv_schema; Owner: postgres
--

CREATE SEQUENCE sgv_schema.seller_id_seller_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE sgv_schema.seller_id_seller_seq OWNER TO postgres;

--
-- Name: seller_id_seller_seq; Type: SEQUENCE OWNED BY; Schema: sgv_schema; Owner: postgres
--

ALTER SEQUENCE sgv_schema.seller_id_seller_seq OWNED BY sgv_schema.seller.id_seller;


--
-- Name: client id_client; Type: DEFAULT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.client ALTER COLUMN id_client SET DEFAULT nextval('sgv_schema.client_id_client_seq'::regclass);


--
-- Name: item_requested id_item; Type: DEFAULT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.item_requested ALTER COLUMN id_item SET DEFAULT nextval('sgv_schema.item_requested_id_item_seq'::regclass);


--
-- Name: manifacturer id_manifacturer; Type: DEFAULT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.manifacturer ALTER COLUMN id_manifacturer SET DEFAULT nextval('sgv_schema.manifacturer_id_manifacturer_seq'::regclass);


--
-- Name: office id_office; Type: DEFAULT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.office ALTER COLUMN id_office SET DEFAULT nextval('sgv_schema.office_id_office_seq'::regclass);


--
-- Name: product id_product; Type: DEFAULT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.product ALTER COLUMN id_product SET DEFAULT nextval('sgv_schema.product_id_product_seq'::regclass);


--
-- Name: product_entry id_entry; Type: DEFAULT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.product_entry ALTER COLUMN id_entry SET DEFAULT nextval('sgv_schema.product_entry_id_entry_seq'::regclass);


--
-- Name: requested id_requested; Type: DEFAULT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.requested ALTER COLUMN id_requested SET DEFAULT nextval('sgv_schema.requested_id_requested_seq'::regclass);


--
-- Name: sale id_sale; Type: DEFAULT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.sale ALTER COLUMN id_sale SET DEFAULT nextval('sgv_schema.sale_id_sale_seq'::regclass);


--
-- Name: sales_target id_sales_target; Type: DEFAULT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.sales_target ALTER COLUMN id_sales_target SET DEFAULT nextval('sgv_schema.sales_target_id_sales_target_seq'::regclass);


--
-- Name: seller id_seller; Type: DEFAULT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.seller ALTER COLUMN id_seller SET DEFAULT nextval('sgv_schema.seller_id_seller_seq'::regclass);


--
-- Data for Name: client; Type: TABLE DATA; Schema: sgv_schema; Owner: postgres
--

COPY sgv_schema.client (id_client, name_client, birth_date, created_at, is_active, cell_phone) FROM stdin;
1	João Silva	1985-03-12	2026-03-13 01:56:46.845777	t	11988881111
2	Maria Oliveira	1992-07-25	2026-03-13 01:56:46.845777	t	21977772222
3	Pedro Santos	1978-12-02	2026-03-13 01:56:46.845777	t	31966663333
4	Ana Costa	2000-05-18	2026-03-13 01:56:46.845777	t	41955554444
5	Lucas Pereira	1995-10-30	2026-03-13 01:56:46.845777	t	51944445555
6	Beatriz Souza	1989-01-22	2026-03-13 01:56:46.845777	t	61933336666
7	Marcos Rocha	1982-08-14	2026-03-13 01:56:46.845777	t	71922227777
8	Fernanda Lima	1997-04-05	2026-03-13 01:56:46.845777	t	81911118888
9	Ricardo Dias	1975-11-11	2026-03-13 01:56:46.845777	t	91999990000
10	Julia Martins	2002-09-09	2026-03-13 01:56:46.845777	t	11912345678
\.


--
-- Data for Name: item_requested; Type: TABLE DATA; Schema: sgv_schema; Owner: postgres
--

COPY sgv_schema.item_requested (id_item, id_requested, id_product, quantity, unit_measure, price, date) FROM stdin;
21	1	2	1.00	UN	5500.00	2026-03-13 12:22:27.20778
22	1	4	2.00	UN	250.00	2026-03-13 12:22:27.20778
23	2	2	1.00	UN	1250.00	2026-03-13 12:22:27.20778
24	2	3	1.00	UN	380.00	2026-03-13 12:22:27.20778
25	3	5	1.00	UN	420.00	2026-03-13 12:22:27.20778
26	4	16	1.00	UN	2900.00	2026-03-13 12:22:27.20778
27	5	7	1.00	UN	1150.00	2026-03-13 12:22:27.20778
28	6	9	10.50	KG	620.00	2026-03-13 12:22:27.20778
29	7	11	1.00	UN	550.00	2026-03-13 12:22:27.20778
30	8	12	2.00	UN	1850.00	2026-03-13 12:22:27.20778
31	9	15	1.00	UN	210.00	2026-03-13 12:22:27.20778
32	10	20	1.00	UN	780.00	2026-03-13 12:22:27.20778
33	1	2	1.00	UN	5500.00	2026-03-13 12:22:27.20778
34	5	6	1.00	UN	890.00	2026-03-13 12:22:27.20778
35	8	8	1.00	UN	740.00	2026-03-13 12:22:27.20778
36	9	10	1.00	UN	480.00	2026-03-13 12:22:27.20778
37	10	13	1.00	UN	320.00	2026-03-13 12:22:27.20778
38	7	14	5.00	UN	120.00	2026-03-13 12:22:27.20778
39	6	17	1.00	UN	3400.00	2026-03-13 12:22:27.20778
40	4	18	1.00	UN	950.00	2026-03-13 12:22:27.20778
\.


--
-- Data for Name: manifacturer; Type: TABLE DATA; Schema: sgv_schema; Owner: postgres
--

COPY sgv_schema.manifacturer (id_manifacturer, cnpj_manifacture, name_manifacture, cell_phone, is_active, created_at) FROM stdin;
1	12345678000101 	TechCorp Brasil	11911112222	t	2024-01-01 08:00:00
2	98765432000102 	Global Pecas	21922223333	t	2024-01-02 09:30:00
3	11222333000103 	Industria Metalurgica	31933334444	t	2024-01-03 10:15:00
4	44555666000104 	Logistica Express SA	41944445555	t	2024-01-04 14:20:00
5	77888999000105 	Solucoes em Plasticos	51955556666	t	2024-01-05 16:45:00
6	10111213000106 	Fabrica de Eletronicos	61966667777	t	2024-01-06 08:10:00
7	14151617000107 	Moveis de Escritorio	71977778888	t	2024-01-07 13:50:00
8	18192021000108 	Vidros e Cristais	81988889999	t	2024-01-08 17:05:00
9	22232425000109 	Sistemas de Refrigeracao	91999990000	t	2024-01-09 12:30:00
10	26272829000110 	Embalagens Modernas	11912345678	t	2024-01-10 10:00:00
\.


--
-- Data for Name: office; Type: TABLE DATA; Schema: sgv_schema; Owner: postgres
--

COPY sgv_schema.office (id_office, city_office, region, id_manager_office) FROM stdin;
1	São Paulo	SOUTH	1
2	Manaus	NORTH	2
3	Recife	EAST	3
4	Cuiabá	WEST	4
5	Porto Alegre	SOUTH	5
\.


--
-- Data for Name: product; Type: TABLE DATA; Schema: sgv_schema; Owner: postgres
--

COPY sgv_schema.product (id_product, name_product, description, is_active, price_product, sku, created_at) FROM stdin;
2	Notebook Gamer i7	Processador i7, 16GB RAM, SSD 512GB, RTX 3060	t	5500.00	8973724	2026-03-13 14:36:47.866877
3	Monitor 24 Polegadas	Monitor LED Full HD 144Hz 1ms	t	1250.00	5249595	2026-03-13 14:36:47.866877
4	Teclado Mecanico RGB	Switch Blue com retroiluminacao customizavel	t	380.00	7975994	2026-03-13 14:36:47.866877
5	Mouse Gamer Pro	Sensor de 16000 DPI, 6 botoes programaveis	t	250.00	4778726	2026-03-13 14:36:47.866877
6	Headset 7.1 Surround	Microfone com cancelamento de ruido	t	420.00	5186759	2026-03-13 14:36:47.866877
7	Cadeira Ergonomica	Ajuste de altura e lombar, couro sintético	t	1150.00	6724449	2026-03-13 14:36:47.866877
8	Webcam 4K Ultra HD	Foco automatico e microfone estereo integrado	t	890.00	669754	2026-03-13 14:36:47.866877
9	Mesa Escritorio em L	Estrutura metalica e tampo em madeira MDF	t	740.00	7820952	2026-03-13 14:36:47.866877
10	SSD 1TB NVMe Gen4	Velocidade de leitura ate 7000MB/s	t	620.00	1567790	2026-03-13 14:36:47.866877
11	HD Externo 2TB	Conexao USB 3.0, portatil e resistente	t	480.00	5130792	2026-03-13 14:36:47.866877
12	Roteador Wi-Fi 6 Giga	Quad-core, suporte a mais de 50 dispositivos	t	550.00	7437761	2026-03-13 14:36:47.866877
13	Impressora Laser Color	Impressao rapida e conexao Wi-Fi Direct	t	1850.00	9904575	2026-03-13 14:36:47.866877
14	Suporte Monitor Triplo	Bracos articulados com ajuste de pressao	t	320.00	4727306	2026-03-13 14:36:47.866877
15	Filtro de Linha Profissional	8 tomadas com protecao contra surtos	t	120.00	5717358	2026-03-13 14:36:47.866877
16	Adaptador Hub USB-C	Saida HDMI 4K, 3 portas USB 3.0 e SD Card	t	210.00	17892	2026-03-13 14:36:47.866877
17	Smartphone Android X	Tela 6.7 OLED, Camera Tripla 108MP	t	2900.00	1130095	2026-03-13 14:36:47.866877
18	Tablet Design Pro	Caneta inclusa, 256GB armazenamento	t	3400.00	1948869	2026-03-13 14:36:47.866877
19	Smartwatch Fitness	Monitoramento de sono e batimentos cardiacos	t	950.00	3920108	2026-03-13 14:36:47.866877
20	Caixa de Som Hi-Fi	Conexao optica e Bluetooth 5.0	t	1300.00	9819909	2026-03-13 14:36:47.866877
21	Microfone Estúdio Condensador	Padrao polar cardioide, conexao XLR/USB	t	780.00	8687281	2026-03-13 14:36:47.866877
\.


--
-- Data for Name: product_entry; Type: TABLE DATA; Schema: sgv_schema; Owner: postgres
--

COPY sgv_schema.product_entry (id_entry, id_product, id_office, id_seller_responsable, id_manifacturer, quantity, unit_measure, date_entry) FROM stdin;
21	2	1	1	1	150.00	UN	2026-03-13 12:10:32.599772
22	2	2	2	2	10.50	KG	2026-03-13 12:10:32.599772
23	3	3	3	3	50.00	UN	2026-03-13 12:10:32.599772
24	4	4	4	4	20.00	UN	2026-03-13 12:10:32.599772
25	5	5	5	5	100.00	UN	2026-03-13 12:10:32.599772
26	6	1	6	6	5.75	KG	2026-03-13 12:10:32.599772
27	7	2	7	7	30.00	UN	2026-03-13 12:10:32.599772
28	8	3	8	8	12.00	UN	2026-03-13 12:10:32.599772
29	9	4	9	9	80.00	UN	2026-03-13 12:10:32.599772
30	10	5	10	10	45.00	UN	2026-03-13 12:10:32.599772
31	11	1	1	1	200.00	UN	2026-03-13 12:10:32.599772
32	12	2	2	2	8.00	KG	2026-03-13 12:10:32.599772
33	13	3	3	3	15.00	UN	2026-03-13 12:10:32.599772
34	14	4	4	4	60.00	UN	2026-03-13 12:10:32.599772
35	15	5	5	5	25.00	UN	2026-03-13 12:10:32.599772
36	16	1	6	6	40.00	UN	2026-03-13 12:10:32.599772
37	17	2	7	7	10.50	KG	2026-03-13 12:10:32.599772
38	18	3	8	8	90.00	UN	2026-03-13 12:10:32.599772
39	19	4	9	9	11.00	UN	2026-03-13 12:10:32.599772
40	20	5	10	10	33.00	UN	2026-03-13 12:10:32.599772
\.


--
-- Data for Name: requested; Type: TABLE DATA; Schema: sgv_schema; Owner: postgres
--

COPY sgv_schema.requested (id_requested, id_client, id_office, status_requested, created_at, update_at) FROM stdin;
1	1	1	DRAFT	2026-03-13 12:17:27.71854	2026-03-13 12:17:27.71854
2	2	2	PROCESSED	2026-03-13 12:17:27.71854	2026-03-13 12:17:27.71854
3	3	3	DRAFT	2026-03-13 12:17:27.71854	2026-03-13 12:17:27.71854
4	4	4	CANCELED	2026-03-13 12:17:27.71854	2026-03-13 12:17:27.71854
5	5	5	PROCESSED	2026-03-13 12:17:27.71854	2026-03-13 12:17:27.71854
6	6	1	DRAFT	2026-03-13 12:17:27.71854	2026-03-13 12:17:27.71854
7	7	2	PROCESSED	2026-03-13 12:17:27.71854	2026-03-13 12:17:27.71854
8	8	3	DRAFT	2026-03-13 12:17:27.71854	2026-03-13 12:17:27.71854
9	9	4	PROCESSED	2026-03-13 12:17:27.71854	2026-03-13 12:17:27.71854
10	10	5	CANCELED	2026-03-13 12:17:27.71854	2026-03-13 12:17:27.71854
\.


--
-- Data for Name: sale; Type: TABLE DATA; Schema: sgv_schema; Owner: postgres
--

COPY sgv_schema.sale (id_sale, id_client, id_seller, id_requested, id_office, amount_total, date_sale) FROM stdin;
511	2	2	2	2	1184.01	2026-03-13 15:34:09.494472
512	3	3	3	3	2743.49	2026-03-13 15:33:09.494472
513	4	4	4	4	795.99	2026-03-13 15:32:09.494472
514	5	5	5	5	4416.02	2026-03-13 15:31:09.494472
515	6	6	6	1	1495.76	2026-03-13 15:30:09.494472
516	7	7	7	2	807.48	2026-03-13 15:29:09.494472
517	8	8	8	3	849.66	2026-03-13 15:28:09.494472
518	9	9	9	4	4515.10	2026-03-13 15:27:09.494472
519	10	10	10	5	2613.57	2026-03-13 15:26:09.494472
520	1	1	1	1	3971.47	2026-03-13 15:25:09.494472
\.


--
-- Data for Name: sales_target; Type: TABLE DATA; Schema: sgv_schema; Owner: postgres
--

COPY sgv_schema.sales_target (id_sales_target, id_office, sales_target, start_date, end_date) FROM stdin;
1	1	500000.00	2024-01-01	2024-06-30
2	2	350000.00	2024-01-01	2024-06-30
3	3	420000.00	2024-01-01	2024-06-30
4	4	280000.00	2024-01-01	2024-06-30
5	5	480000.00	2024-01-01	2024-06-30
6	1	600000.00	2024-07-01	2024-12-31
7	2	400000.00	2024-07-01	2024-12-31
8	3	450000.00	2024-07-01	2024-12-31
9	4	300000.00	2024-07-01	2024-12-31
10	5	520000.00	2024-07-01	2024-12-31
\.


--
-- Data for Name: seller; Type: TABLE DATA; Schema: sgv_schema; Owner: postgres
--

COPY sgv_schema.seller (id_seller, id_office, name_seller, date_birth, hiring_date, level_seller, create_date, is_active) FROM stdin;
1	1	Ricardo Silva	1980-05-15	2015-02-10	MANAGER	2026-03-13 01:59:46.954712	t
6	1	Eduarda Rocha	1994-01-25	2021-05-20	SELLER	2026-03-13 01:59:46.954712	t
2	2	Ana Oliveira	1988-03-22	2016-06-15	MANAGER	2026-03-13 01:59:46.954712	t
7	2	Fabio Santos	1982-12-05	2017-11-10	SELLER	2026-03-13 01:59:46.954712	t
3	3	Bruno Costa	1992-09-10	2018-01-20	MANAGER	2026-03-13 01:59:46.954712	t
8	3	Giovana Dias	1996-04-18	2022-01-15	SELLER	2026-03-13 01:59:46.954712	t
4	4	Carla Souza	1985-11-30	2015-08-05	MANAGER	2026-03-13 01:59:46.954712	t
9	4	Henrique Melo	1989-08-20	2018-09-01	SELLER	2026-03-13 01:59:46.954712	t
5	5	Daniel Lima	1990-07-12	2019-03-12	MANAGER	2026-03-13 01:59:46.954712	t
10	5	Isabela Cruz	2001-02-28	2023-06-01	SELLER	2026-03-13 01:59:46.954712	t
\.


--
-- Name: client_id_client_seq; Type: SEQUENCE SET; Schema: sgv_schema; Owner: postgres
--

SELECT pg_catalog.setval('sgv_schema.client_id_client_seq', 1, false);


--
-- Name: item_requested_id_item_seq; Type: SEQUENCE SET; Schema: sgv_schema; Owner: postgres
--

SELECT pg_catalog.setval('sgv_schema.item_requested_id_item_seq', 40, true);


--
-- Name: manifacturer_id_manifacturer_seq; Type: SEQUENCE SET; Schema: sgv_schema; Owner: postgres
--

SELECT pg_catalog.setval('sgv_schema.manifacturer_id_manifacturer_seq', 10, true);


--
-- Name: office_id_office_seq; Type: SEQUENCE SET; Schema: sgv_schema; Owner: postgres
--

SELECT pg_catalog.setval('sgv_schema.office_id_office_seq', 1, false);


--
-- Name: product_entry_id_entry_seq; Type: SEQUENCE SET; Schema: sgv_schema; Owner: postgres
--

SELECT pg_catalog.setval('sgv_schema.product_entry_id_entry_seq', 40, true);


--
-- Name: product_id_product_seq; Type: SEQUENCE SET; Schema: sgv_schema; Owner: postgres
--

SELECT pg_catalog.setval('sgv_schema.product_id_product_seq', 21, true);


--
-- Name: requested_id_requested_seq; Type: SEQUENCE SET; Schema: sgv_schema; Owner: postgres
--

SELECT pg_catalog.setval('sgv_schema.requested_id_requested_seq', 10, true);


--
-- Name: sale_id_sale_seq; Type: SEQUENCE SET; Schema: sgv_schema; Owner: postgres
--

SELECT pg_catalog.setval('sgv_schema.sale_id_sale_seq', 520, true);


--
-- Name: sales_target_id_sales_target_seq; Type: SEQUENCE SET; Schema: sgv_schema; Owner: postgres
--

SELECT pg_catalog.setval('sgv_schema.sales_target_id_sales_target_seq', 10, true);


--
-- Name: seller_id_seller_seq; Type: SEQUENCE SET; Schema: sgv_schema; Owner: postgres
--

SELECT pg_catalog.setval('sgv_schema.seller_id_seller_seq', 1, false);


--
-- Name: client client_pkey; Type: CONSTRAINT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.client
    ADD CONSTRAINT client_pkey PRIMARY KEY (id_client);


--
-- Name: item_requested item_requested_pkey; Type: CONSTRAINT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.item_requested
    ADD CONSTRAINT item_requested_pkey PRIMARY KEY (id_item);


--
-- Name: manifacturer manifacturer_pkey; Type: CONSTRAINT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.manifacturer
    ADD CONSTRAINT manifacturer_pkey PRIMARY KEY (id_manifacturer);


--
-- Name: office office_pkey; Type: CONSTRAINT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.office
    ADD CONSTRAINT office_pkey PRIMARY KEY (id_office);


--
-- Name: product_entry product_entry_pkey; Type: CONSTRAINT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.product_entry
    ADD CONSTRAINT product_entry_pkey PRIMARY KEY (id_entry);


--
-- Name: product product_pkey; Type: CONSTRAINT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.product
    ADD CONSTRAINT product_pkey PRIMARY KEY (id_product);


--
-- Name: requested requested_pkey; Type: CONSTRAINT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.requested
    ADD CONSTRAINT requested_pkey PRIMARY KEY (id_requested);


--
-- Name: sale sale_pkey; Type: CONSTRAINT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.sale
    ADD CONSTRAINT sale_pkey PRIMARY KEY (id_sale);


--
-- Name: sales_target sales_target_pkey; Type: CONSTRAINT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.sales_target
    ADD CONSTRAINT sales_target_pkey PRIMARY KEY (id_sales_target);


--
-- Name: seller seller_pkey; Type: CONSTRAINT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.seller
    ADD CONSTRAINT seller_pkey PRIMARY KEY (id_seller);


--
-- Name: sale fk_client; Type: FK CONSTRAINT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.sale
    ADD CONSTRAINT fk_client FOREIGN KEY (id_client) REFERENCES sgv_schema.client(id_client) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: requested fk_client_requested; Type: FK CONSTRAINT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.requested
    ADD CONSTRAINT fk_client_requested FOREIGN KEY (id_client) REFERENCES sgv_schema.client(id_client) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: office fk_manager_seller; Type: FK CONSTRAINT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.office
    ADD CONSTRAINT fk_manager_seller FOREIGN KEY (id_manager_office) REFERENCES sgv_schema.seller(id_seller);


--
-- Name: product_entry fk_manufacturer; Type: FK CONSTRAINT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.product_entry
    ADD CONSTRAINT fk_manufacturer FOREIGN KEY (id_manifacturer) REFERENCES sgv_schema.manifacturer(id_manifacturer) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: sale fk_office; Type: FK CONSTRAINT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.sale
    ADD CONSTRAINT fk_office FOREIGN KEY (id_office) REFERENCES sgv_schema.office(id_office) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: product_entry fk_office_entry; Type: FK CONSTRAINT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.product_entry
    ADD CONSTRAINT fk_office_entry FOREIGN KEY (id_office) REFERENCES sgv_schema.office(id_office) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: requested fk_office_requested; Type: FK CONSTRAINT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.requested
    ADD CONSTRAINT fk_office_requested FOREIGN KEY (id_office) REFERENCES sgv_schema.office(id_office) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: sales_target fk_office_target; Type: FK CONSTRAINT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.sales_target
    ADD CONSTRAINT fk_office_target FOREIGN KEY (id_office) REFERENCES sgv_schema.office(id_office);


--
-- Name: item_requested fk_product_entry; Type: FK CONSTRAINT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.item_requested
    ADD CONSTRAINT fk_product_entry FOREIGN KEY (id_product) REFERENCES sgv_schema.product(id_product) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: product_entry fk_product_entry; Type: FK CONSTRAINT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.product_entry
    ADD CONSTRAINT fk_product_entry FOREIGN KEY (id_product) REFERENCES sgv_schema.product(id_product) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: item_requested fk_requested; Type: FK CONSTRAINT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.item_requested
    ADD CONSTRAINT fk_requested FOREIGN KEY (id_requested) REFERENCES sgv_schema.requested(id_requested) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: sale fk_requested; Type: FK CONSTRAINT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.sale
    ADD CONSTRAINT fk_requested FOREIGN KEY (id_requested) REFERENCES sgv_schema.requested(id_requested) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: sale fk_seller; Type: FK CONSTRAINT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.sale
    ADD CONSTRAINT fk_seller FOREIGN KEY (id_seller) REFERENCES sgv_schema.seller(id_seller) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: seller fk_seller_office; Type: FK CONSTRAINT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.seller
    ADD CONSTRAINT fk_seller_office FOREIGN KEY (id_office) REFERENCES sgv_schema.office(id_office) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: product_entry fk_seller_responsable; Type: FK CONSTRAINT; Schema: sgv_schema; Owner: postgres
--

ALTER TABLE ONLY sgv_schema.product_entry
    ADD CONSTRAINT fk_seller_responsable FOREIGN KEY (id_seller_responsable) REFERENCES sgv_schema.seller(id_seller) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- PostgreSQL database dump complete
--

\unrestrict 8wIYYHjc2NTvcux1cozdIhWNycfBRgl3hReHMZvbxhuKbfpVxbcCc1fgxWJ7BKr

