/**   
 * License Agreement for OpenSearchServer
 *
 * Copyright (C) 2008-2011 Emmanuel Keller / Jaeksoft
 * 
 * http://www.open-search-server.com
 * 
 * This file is part of OpenSearchServer.
 *
 * OpenSearchServer is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 * OpenSearchServer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenSearchServer. 
 *  If not, see <http://www.gnu.org/licenses/>.
 **/

package com.jaeksoft.searchlib.filter;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.util.OpenBitSet;

import com.jaeksoft.searchlib.index.ReaderLocal;

public class FilterHits extends org.apache.lucene.search.Filter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 966120808560552509L;

	protected OpenBitSet docSet;

	protected FilterHits() {
		docSet = null;
	}

	protected void and(FilterHits filterHits) {
		if (docSet == null)
			docSet = (OpenBitSet) filterHits.docSet.clone();
		else
			docSet.and(filterHits.docSet);
	}

	public FilterHits(Query query, boolean negative, ReaderLocal reader)
			throws IOException, ParseException {
		FilterCollector collector = new FilterCollector(reader.maxDoc());
		reader.search(query, null, collector);
		docSet = collector.bitSet;
		if (negative)
			docSet.flip(0, docSet.size());

	}

	private class FilterCollector extends Collector {

		private OpenBitSet bitSet;

		private FilterCollector(int size) {
			this.bitSet = new OpenBitSet(size);
		}

		@Override
		public void collect(int docId) {
			bitSet.set(docId);
		}

		@Override
		public boolean acceptsDocsOutOfOrder() {
			return true;
		}

		@Override
		public void setNextReader(IndexReader reader, int id)
				throws IOException {
		}

		@Override
		public void setScorer(Scorer arg0) throws IOException {
		}
	}

	@Override
	public DocIdSet getDocIdSet(IndexReader reader) throws IOException {
		return this.docSet;
	}

}
