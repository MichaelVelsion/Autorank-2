package me.armar.plugins.autorank.playerchecker;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.playerchecker.requirement.Requirement;
import me.armar.plugins.autorank.playerchecker.result.Result;

import org.bukkit.entity.Player;

public class RankChange {

	private final String rank;
	private final List<Requirement> req;
	private final List<Result> res;
	private final String rankTo;
	private final Autorank plugin;  

	public RankChange(final Autorank plugin, final String rank,
			final String rankTo, final List<Requirement> req,
			final List<Result> res) {
		this.rank = rank;
		this.req = req;
		this.res = res;
		this.rankTo = rankTo;
		this.plugin = plugin;
	}

	public String getRank() {
		return rank;
	}

	public List<Requirement> getReq() {
		return req;
	}

	public List<Result> getRes() {
		return res;
	}

	public String getRankTo() {
		return rankTo;
	}

	public boolean hasRankUp() {
		return rankTo != null;
	}

	public boolean checkRequirements(final Player player) {
		boolean result = true;

		for (final Requirement r : req) {
			if (r == null)
				return false;

			final int reqID = r.getReqId();

			// When optional, always true
			if (r.isOptional())
				continue;

			if (!r.useAutoCompletion()
					&& !plugin.getRequirementHandler().hasCompletedRequirement(
							reqID, player.getName())) {
				result = false;
				break;
			}

			if (!r.meetsRequirement(player)) {

				// Player does not meet requirement, but has completed it already
				if (plugin.getRequirementHandler().hasCompletedRequirement(
						reqID, player.getName())) {
					continue;
				}
				result = false;
				break;
			} else {
				// Player meets requirement, thus perform results of requirement
				// Perform results of a requirement as well
				final List<Result> results = r.getResults();

				// Player has not completed this requirement -> perform results
				if (!plugin.getRequirementHandler().hasCompletedRequirement(
						reqID, player.getName())) {
					plugin.getRequirementHandler().addPlayerProgress(
							player.getName(), reqID);
				} else {
					// Player already completed this -> do nothing
					continue;
				}

				boolean noErrors = true;
				for (final Result realResult : results) {

					if (!realResult.applyResult(player)) {
						noErrors = false;
					}
				}
				result = noErrors;
			}
		}

		return result;
	}

	public List<Requirement> getFailedRequirements(final Player player) {
		final List<Requirement> failed = new CopyOnWriteArrayList<Requirement>();
		failed.addAll(req);

		for (final Requirement r : failed) {
			if (r != null)
				if (r.meetsRequirement(player)) {
					failed.remove(r);
				}
		}

		return failed;
	}

	public boolean applyChange(final Player player, final String group) {
		boolean result = true;

		if (checkRequirements(player)) {
			for (final Result r : res) {
				if (r != null)
					if (!r.applyResult(player, group))
						result = false;
			}
		} else {
			result = false;
		}

		return result;
	}

	@Override
	public String toString() {
		final StringBuilder b = new StringBuilder();
		b.append(rank);
		b.append(": ");

		boolean first = true;
		for (final Requirement r : req) {
			if (!first)
				b.append(", ");
			first = false;
			b.append(r.toString());
		}

		b.append(" -> ");

		first = true;
		for (final Result r : res) {
			if (!first)
				b.append(", ");
			first = false;
			if (r != null) {
				b.append(r.toString());
			} else {
				b.append("NULL");
			}

		}
		return b.toString();
	}

}
