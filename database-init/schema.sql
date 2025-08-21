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
        ON DELETE CASCADE
        NOT VALID,
    CONSTRAINT book_genres_genre_id_fkey FOREIGN KEY (genre_id)
        REFERENCES public.genres (genre_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
        NOT VALID
);

INSERT INTO public.genres (genre_name_en, genre_name_ru) VALUES('action', 'боевик'),('adventure', 'приключения'),('anthropology', 'антропология'),('astronomy', 'астрономия'),('archaeology', 'археология'),('architecture', 'архитектура'),('art', 'искусство'),('aviation', 'авиация'),('biography', 'биография'),('biology', 'биология'),('business', 'бизнес'),('chemistry', 'химия'),('children', 'детская литература'),('classics', 'классика'),('contemporary', 'современная литература'),('cookbook', 'кулинарная книга'),('crafts', 'рукоделие'),('crime', 'криминал'),('dystopia', 'антиутопия'),('economics', 'экономика'),('education', 'образование'),('engineering', 'инженерия'),('environment', 'окружающая среда'),('erotica', 'эротика'),('essay', 'эссе'),('fairy_tales', 'сказки'),('fantasy', 'фэнтези'),('fashion', 'мода'),('fiction', 'художественная литература'),('finance', 'финансы'),('folklore', 'фольклор'),('food', 'еда'),('gaming', 'игры'),('gardening', 'садоводство'),('geography', 'география'),('geology', 'геология'),('graphic_novel', 'графический роман'),('health', 'здоровье'),('historical', 'исторический'),('historical_fiction', 'исторический роман'),('history', 'история'),('horror', 'ужасы'),('how_to', 'руководство'),('humor', 'юмор'),('inspirational', 'вдохновляющая литература'),('journalism', 'журналистика'),('law', 'право'),('literature', 'литература'),('magical_realism', 'магический реализм'),('manga', 'манга'),('martial_arts', 'боевые искусства'),('mathematics', 'математика'),('medicine', 'медицина'),('medieval', 'средневековье'),('memoir', 'мемуары'),('mystery', 'детектив'),('mythology', 'мифология'),('nature', 'природа'),('nonfiction', 'научно-популярная литература'),('novel', 'роман'),('occult', 'оккультизм'),('paranormal', 'паранормальное'),('parenting', 'воспитание'),('philosophy', 'философия'),('physics', 'физика'),('picture_book', 'книжка с картинками'),('poetry', 'поэзия'),('politics', 'политика'),('programming', 'программирование'),('psychology', 'психология'),('reference', 'справочная литература'),('relationships', 'отношения'),('religion', 'религия'),('romance', 'романтика'),('science_and_technology', 'наука и технологии'),('science_fiction', 'научная фантастика'),('self_help', 'самопомощь'),('short_stories', 'рассказы'),('society', 'общество'),('sociology', 'социология'),('space', 'космос'),('spirituality', 'духовность'),('sports', 'спорт'),('text_book', 'учебник'),('thriller', 'триллер'),('travel', 'путешествия'),('true_crime', 'настоящее преступление'),('war', 'война'),('writing', 'писательское мастерство'),('young_adult', 'подростковая литература');
