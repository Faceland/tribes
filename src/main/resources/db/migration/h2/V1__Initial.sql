--
-- This file is part of Tribes, licensed under the ISC License.
--
-- Copyright (c) 2015 Richard Harrah
--
-- Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted,
-- provided that the above copyright notice and this permission notice appear in all copies.
--
-- THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL
-- IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
-- INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
-- ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF
-- THIS SOFTWARE.
--

CREATE TABLE tr_cells (
    world VARCHAR(60) NOT NULL,
    x INT NOT NULL,
    z INT NOT NULL,
    owner VARCHAR(20),
    PRIMARY KEY (world, x, z)
);

CREATE TABLE tr_members (
    id VARCHAR(20) NOT NULL PRIMARY KEY,
    tribe VARCHAR(20),
    rank VARCHAR(20)
);

CREATE TABLE tr_tribes (
    id VARCHAR(20) NOT NULL PRIMARY KEY,
    owner VARCHAR(20) NOT NULL UNIQUE
);