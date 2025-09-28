CREATE TABLE IF NOT EXISTS public.users
(
    user_id uuid NOT NULL,
    email text NOT NULL,
    password_hash text NOT NULL,
    is_verified boolean NOT NULL DEFAULT false,
    CONSTRAINT users_pkey PRIMARY KEY (user_id),
    CONSTRAINT users_email_key UNIQUE (email)
);

CREATE TYPE book_status_enum AS ENUM ('WANT_TO_READ', 'READING', 'READ');

CREATE TABLE IF NOT EXISTS public.books
(
    book_id uuid NOT NULL,
    user_id uuid NOT NULL,
    title text NOT NULL,
    author text NOT NULL,
    cover_url text,
    status book_status_enum NOT NULL,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    CONSTRAINT books_pkey PRIMARY KEY (book_id),
    CONSTRAINT books_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES public.users (user_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.genres
(
    genre_id serial NOT NULL,
    genre_name_en text NOT NULL,
    genre_name_ru text NOT NULL,
    CONSTRAINT genres_pkey PRIMARY KEY (genre_id),
    CONSTRAINT genres_genre_name_en_key UNIQUE (genre_name_en),
    CONSTRAINT genres_genre_name_ru_key UNIQUE (genre_name_ru)
);

CREATE TABLE IF NOT EXISTS public.book_genres
(
    book_id uuid NOT NULL,
    genre_id integer NOT NULL,
    CONSTRAINT book_genres_pkey PRIMARY KEY (book_id, genre_id),
    CONSTRAINT book_genres_book_id_fkey FOREIGN KEY (book_id)
        REFERENCES public.books (book_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE,
    CONSTRAINT book_genres_genre_id_fkey FOREIGN KEY (genre_id)
        REFERENCES public.genres (genre_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.email_verifications
(
    user_id uuid NOT NULL,
    code character varying(6) NOT NULL,
    expires_at timestamp with time zone NOT NULL,
    CONSTRAINT email_verifications_pkey PRIMARY KEY (user_id),
    CONSTRAINT email_verifications_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES public.users (user_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.refresh_tokens
(
    token character varying(256) NOT NULL,
    user_id uuid NOT NULL,
    expires_at timestamp with time zone NOT NULL,
    CONSTRAINT refresh_tokens_pkey PRIMARY KEY (token),
    CONSTRAINT refresh_tokens_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES public.users (user_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);