CREATE TYPE book_status_enum AS ENUM ('WANT_TO_READ', 'READING', 'READ');

CREATE TABLE IF NOT EXISTS public.books
(
    book_id uuid NOT NULL,
    title text NOT NULL,
    author text NOT NULL,
    cover_path text,
    status book_status_enum NOT NULL,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    CONSTRAINT books_pkey PRIMARY KEY (book_id)
);

CREATE TABLE IF NOT EXISTS public.genres
(
    genre_id serial NOT NULL,
    genre_name text NOT NULL,
    CONSTRAINT genres_pkey PRIMARY KEY (genre_id),
    CONSTRAINT genres_genre_name_key UNIQUE (genre_name)
);

CREATE TABLE IF NOT EXISTS public.book_genres
(
    book_id uuid NOT NULL,
    genre_id integer NOT NULL,
    CONSTRAINT book_genres_pkey PRIMARY KEY (book_id, genre_id),
    CONSTRAINT book_genres_book_id_fkey FOREIGN KEY (book_id)
        REFERENCES public.books (book_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
        NOT VALID,
    CONSTRAINT book_genres_genre_id_fkey FOREIGN KEY (genre_id)
        REFERENCES public.genres (genre_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
        NOT VALID
);

INSERT INTO public.genres (genre_name) VALUES ('боевик'), ('приключения'), ('антропология'), ('астрономия'), ('археология'), ('архитектура'), ('искусство'), ('авиация'), ('биография'), ('биология'), ('бизнес'), ('химия'), ('детская литература'), ('классика'), ('современная литература'), ('кулинарная книга'), ('рукоделие'), ('криминал'), ('антиутопия'), ('экономика'), ('образование'), ('инженерия'), ('окружающая среда'), ('эротика'), ('эссе'), ('сказки'), ('фэнтези'), ('мода'), ('художественная литература'), ('финансы'), ('фольклор'), ('еда'), ('игры'), ('садоводство'), ('география'), ('геология'), ('графический роман'), ('здоровье'), ('исторический'), ('исторический роман'), ('история'), ('ужасы'), ('руководство'), ('юмор'), ('вдохновляющая литература'), ('журналистика'), ('право'), ('литература'), ('магический реализм'), ('манга'), ('боевые искусства'), ('математика'), ('медицина'), ('средневековье'), ('мемуары'), ('детектив'), ('мифология'), ('природа'), ('научно-популярная литература'), ('роман'), ('оккультизм'), ('паранормальное'), ('воспитание'), ('философия'), ('физика'), ('книжка с картинками'), ('поэзия'), ('политика'), ('программирование'), ('психология'), ('справочная литература'), ('отношения'), ('религия'), ('романтика'), ('наука и технологии'), ('научная фантастика'), ('самопомощь'), ('рассказы'), ('общество'), ('социология'), ('космос'), ('духовность'), ('спорт'), ('учебник'), ('триллер'), ('путешествия'), ('настоящее преступление'), ('война'), ('писательское мастерство'), ('подростковая литература');

