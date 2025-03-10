PGDMP                          }            banking_core    14.17 (Homebrew)    14.17 (Homebrew)     p           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false            q           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false            r           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false            s           1262    16385    banking_core    DATABASE     W   CREATE DATABASE banking_core WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'C';
    DROP DATABASE banking_core;
                yujm    false                        2615    2200    public    SCHEMA        CREATE SCHEMA public;
    DROP SCHEMA public;
                yujm    false            t           0    0    SCHEMA public    COMMENT     6   COMMENT ON SCHEMA public IS 'standard public schema';
                   yujm    false    3            �            1259    16401    account    TABLE     W  CREATE TABLE public.account (
    account_number character(7) NOT NULL,
    citizen_id character varying(20) NOT NULL,
    thai_name character varying(255) NOT NULL,
    en_name character varying(255) NOT NULL,
    current_balance numeric(15,2) NOT NULL,
    pin_hash character varying(255) NOT NULL,
    version integer DEFAULT 0 NOT NULL
);
    DROP TABLE public.account;
       public         heap    postgres    false    3            �            1259    16387    customer    TABLE     �   CREATE TABLE public.customer (
    id integer NOT NULL,
    citizen_id character varying(20) NOT NULL,
    thai_name character varying(255) NOT NULL,
    en_name character varying(255) NOT NULL,
    password_hash character varying(255) NOT NULL
);
    DROP TABLE public.customer;
       public         heap    postgres    false    3            �            1259    16386    customer_id_seq    SEQUENCE     �   CREATE SEQUENCE public.customer_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 &   DROP SEQUENCE public.customer_id_seq;
       public          postgres    false    210    3            u           0    0    customer_id_seq    SEQUENCE OWNED BY     C   ALTER SEQUENCE public.customer_id_seq OWNED BY public.customer.id;
          public          postgres    false    209            �            1259    16420    teller    TABLE     �   CREATE TABLE public.teller (
    employee_id character(9) NOT NULL,
    display_name character varying(100) NOT NULL,
    password_hash character varying(255) NOT NULL
);
    DROP TABLE public.teller;
       public         heap    postgres    false    3            �            1259    16450    transactions    TABLE     M  CREATE TABLE public.transactions (
    id integer NOT NULL,
    reference_id character varying(36) NOT NULL,
    from_account character(7) NOT NULL,
    to_account character(7),
    code character(2) NOT NULL,
    channel character varying(255) NOT NULL,
    transaction_date date NOT NULL,
    transaction_time time without time zone NOT NULL,
    amount numeric(15,2) NOT NULL,
    from_account_balance numeric(15,2) NOT NULL,
    to_account_balance numeric(15,2),
    initiated_by character varying(255) NOT NULL,
    status character varying(255),
    remark character varying(255)
);
     DROP TABLE public.transactions;
       public         heap    postgres    false    3            �            1259    16449    transactions_id_seq    SEQUENCE     �   CREATE SEQUENCE public.transactions_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 *   DROP SEQUENCE public.transactions_id_seq;
       public          postgres    false    214    3            v           0    0    transactions_id_seq    SEQUENCE OWNED BY     K   ALTER SEQUENCE public.transactions_id_seq OWNED BY public.transactions.id;
          public          postgres    false    213            �           2604    16390    customer id    DEFAULT     j   ALTER TABLE ONLY public.customer ALTER COLUMN id SET DEFAULT nextval('public.customer_id_seq'::regclass);
 :   ALTER TABLE public.customer ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    210    209    210            �           2604    16453    transactions id    DEFAULT     r   ALTER TABLE ONLY public.transactions ALTER COLUMN id SET DEFAULT nextval('public.transactions_id_seq'::regclass);
 >   ALTER TABLE public.transactions ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    213    214    214            j          0    16401    account 
   TABLE DATA           u   COPY public.account (account_number, citizen_id, thai_name, en_name, current_balance, pin_hash, version) FROM stdin;
    public          postgres    false    211   c$       i          0    16387    customer 
   TABLE DATA           U   COPY public.customer (id, citizen_id, thai_name, en_name, password_hash) FROM stdin;
    public          postgres    false    210   �%       k          0    16420    teller 
   TABLE DATA           J   COPY public.teller (employee_id, display_name, password_hash) FROM stdin;
    public          postgres    false    212   )&       m          0    16450    transactions 
   TABLE DATA           �   COPY public.transactions (id, reference_id, from_account, to_account, code, channel, transaction_date, transaction_time, amount, from_account_balance, to_account_balance, initiated_by, status, remark) FROM stdin;
    public          postgres    false    214   �&       w           0    0    customer_id_seq    SEQUENCE SET     =   SELECT pg_catalog.setval('public.customer_id_seq', 1, true);
          public          postgres    false    209            x           0    0    transactions_id_seq    SEQUENCE SET     B   SELECT pg_catalog.setval('public.transactions_id_seq', 16, true);
          public          postgres    false    213            �           2606    16408    account account_pkey 
   CONSTRAINT     ^   ALTER TABLE ONLY public.account
    ADD CONSTRAINT account_pkey PRIMARY KEY (account_number);
 >   ALTER TABLE ONLY public.account DROP CONSTRAINT account_pkey;
       public            postgres    false    211            �           2606    16396     customer customer_citizen_id_key 
   CONSTRAINT     a   ALTER TABLE ONLY public.customer
    ADD CONSTRAINT customer_citizen_id_key UNIQUE (citizen_id);
 J   ALTER TABLE ONLY public.customer DROP CONSTRAINT customer_citizen_id_key;
       public            postgres    false    210            �           2606    16400    customer customer_en_name_key 
   CONSTRAINT     [   ALTER TABLE ONLY public.customer
    ADD CONSTRAINT customer_en_name_key UNIQUE (en_name);
 G   ALTER TABLE ONLY public.customer DROP CONSTRAINT customer_en_name_key;
       public            postgres    false    210            �           2606    16394    customer customer_pkey 
   CONSTRAINT     T   ALTER TABLE ONLY public.customer
    ADD CONSTRAINT customer_pkey PRIMARY KEY (id);
 @   ALTER TABLE ONLY public.customer DROP CONSTRAINT customer_pkey;
       public            postgres    false    210            �           2606    16398    customer customer_thai_name_key 
   CONSTRAINT     _   ALTER TABLE ONLY public.customer
    ADD CONSTRAINT customer_thai_name_key UNIQUE (thai_name);
 I   ALTER TABLE ONLY public.customer DROP CONSTRAINT customer_thai_name_key;
       public            postgres    false    210            �           2606    16424    teller teller_pkey 
   CONSTRAINT     Y   ALTER TABLE ONLY public.teller
    ADD CONSTRAINT teller_pkey PRIMARY KEY (employee_id);
 <   ALTER TABLE ONLY public.teller DROP CONSTRAINT teller_pkey;
       public            postgres    false    212            �           2606    16457    transactions transactions_pkey 
   CONSTRAINT     \   ALTER TABLE ONLY public.transactions
    ADD CONSTRAINT transactions_pkey PRIMARY KEY (id);
 H   ALTER TABLE ONLY public.transactions DROP CONSTRAINT transactions_pkey;
       public            postgres    false    214            �           2606    16459 *   transactions transactions_reference_id_key 
   CONSTRAINT     m   ALTER TABLE ONLY public.transactions
    ADD CONSTRAINT transactions_reference_id_key UNIQUE (reference_id);
 T   ALTER TABLE ONLY public.transactions DROP CONSTRAINT transactions_reference_id_key;
       public            postgres    false    214            �           1259    16426    idx_user_accounts    INDEX     O   CREATE INDEX idx_user_accounts ON public.account USING btree (account_number);
 %   DROP INDEX public.idx_user_accounts;
       public            postgres    false    211            j   K  x�m��n�@ ����af.�,E�Ej��8 (Z���mbcL�892��0�<lJ*�e�рn�m�'��xd�yV�aI�L˔�Q�1��%3�Xw](g�#����N�͞�P+][ha$�m���h!d�HuQEd;K;�V�A��i�.�~lM�����jzJ/b}�N\0rȯ���J��-iX$6�1Y|�Y���}6�y��g�mxЄ�'\�N骩r\����Kz�Y^濪�v
�|����Ʀ�հ�Ķ�[� �I�a�7�l�UF�[�{}��>+ ��Y��M����k�􁵸�K%I&�l���!K��R��      i   [   x�3�444226�,�H��L��T1JT14P1Hw
p�*��/	��ͮ��Ouu�����L�)O�L�MOt��25ׯ*��MIu����� �|�      k   [   x�366224TPP�,I��I-�T1JT14PqM,��t���4O��s	�5�(��0��4������)�Lu�K�
)�
�v2p����� a      m   �  x���ϊ$7������˖��,��e��\�M�]H����f�!L�顋E���J�,S��T�VD�C���H�>��ΔI�p"T����?^�����P )1�U�H�#c�IqCLǟ�^�j��ӟ_��������8i_�Jh�٧@��`����u�������e��a�3�9[���[�U��\�_�?�'IXLf5^�Cf^кv�]�[[�=�}�B��j��$��Q�i�_7+r�
�.)͛ձ�c_�n^�_�펲�1m��F�CYT�V��;�F/H�,V���n����7���ģC��[�ߵ� (����+CG3�Xvh�W�RT@[�5?�τ���Z� z � j�"<�3�st}��/%ө=j��) ڤ:�]� ( X�4e'n��G�>��N��[�6n k�� .%?+�ٷ���� �Gx'������S׵ӥ>���U�wi���S�3��j5�W�r��.E��	�����m�1�8px�ÑVJ��Mٳ�+���>�^:�%;�%6C��Yb�l�b2��1�2�T75S�+F>Ly��Z��`�����6YTJpX�?��g.[�K�f��zCRi���V7���A�^"�v�u�F�z���!╮�Q[��9ս�f�Q�D�=6J�Ռ<
�z�b��nN&t���;�H�O�ǉ��v��/�EpV�6���ח3��5MW�cD��%�}Q�݀��p)H��*�Aq�S9�n�qR�/}>)�O���t:�ˆ�     