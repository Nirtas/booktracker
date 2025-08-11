CREATE TABLE IF NOT EXISTS public.books
(
    book_id uuid NOT NULL,
    title character varying(100) NOT NULL,
    author character varying(100) NOT NULL,
    cover_path character varying(255),
    CONSTRAINT books_pkey PRIMARY KEY (book_id)
);
