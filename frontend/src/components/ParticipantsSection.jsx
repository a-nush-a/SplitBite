import React, { useState } from 'react';
import { UserPlus, User, X, Users } from 'lucide-react';

export default function ParticipantsSection({
  participants,
  setParticipants,
  onParticipantRemoved
}) {
  const [nameInput, setNameInput] = useState('');
  const [errorMsg, setErrorMsg] = useState('');

  const handleAddParticipant = (e) => {
    e.preventDefault();
    const cleanName = nameInput.trim();
    if (!cleanName) {
      setErrorMsg('Please enter a participant name.');
      return;
    }

    // Check duplicate
    if (participants.some(p => p.name.toLowerCase() === cleanName.toLowerCase())) {
      setErrorMsg('A participant with this name already exists.');
      return;
    }

    const newParticipant = {
      id: 'p_' + Date.now() + '_' + Math.random().toString(36).substring(2, 6),
      name: cleanName
    };

    setParticipants([...participants, newParticipant]);
    setNameInput('');
    setErrorMsg('');
  };

  const handleRemove = (id) => {
    setParticipants(participants.filter(p => p.id !== id));
    if (onParticipantRemoved) {
      onParticipantRemoved(id);
    }
  };

  return (
    <div className="bg-white rounded-2xl p-6 sm:p-8 border border-slate-200 shadow-sm mb-8">
      <div className="flex items-center space-x-3 mb-6 pb-4 border-b border-slate-100">
        <div className="w-10 h-10 rounded-xl bg-blue-50 text-blue-600 flex items-center justify-center font-bold text-lg">
          2
        </div>
        <div>
          <h2 className="text-xl font-bold text-navy-900">Participants</h2>
          <p className="text-xs text-slate-500">Add the people who shared this meal</p>
        </div>
      </div>

      {/* Input Form */}
      <form onSubmit={handleAddParticipant} className="mb-6">
        <label className="block text-xs font-bold uppercase tracking-wider text-slate-600 mb-2">
          Add Person Name
        </label>
        <div className="flex items-center space-x-2">
          <div className="relative flex-1">
            <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
              <User className="w-4 h-4" />
            </div>
            <input
              type="text"
              value={nameInput}
              onChange={(e) => setNameInput(e.target.value)}
              placeholder="e.g. Rahul, Sneha, Amal, Priya..."
              className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-brand/30 focus:border-brand text-sm font-medium"
            />
          </div>
          <button
            type="submit"
            className="inline-flex items-center space-x-1.5 bg-navy-900 hover:bg-slate-800 text-white font-semibold px-5 py-2.5 rounded-xl shadow-sm transition text-sm"
          >
            <UserPlus className="w-4 h-4 text-brand" />
            <span>Add</span>
          </button>
        </div>

        {errorMsg && (
          <p className="text-xs text-rose-500 font-medium mt-2">{errorMsg}</p>
        )}
      </form>

      {/* Participants List */}
      <div>
        <div className="flex items-center justify-between mb-3">
          <span className="text-xs font-bold uppercase tracking-wider text-slate-500">
            Current Participants ({participants.length})
          </span>
          {participants.length > 0 && (
            <span className="text-[11px] text-slate-400">Click &times; to remove</span>
          )}
        </div>

        {participants.length === 0 ? (
          <div className="text-center py-6 border-2 border-dashed border-slate-200 rounded-xl bg-slate-50/50">
            <Users className="w-8 h-8 text-slate-300 mx-auto mb-1.5" />
            <p className="text-xs font-medium text-slate-500">No participants added yet.</p>
            <p className="text-[11px] text-slate-400">Add at least one person to split the bill.</p>
          </div>
        ) : (
          <div className="flex flex-wrap gap-2.5">
            {participants.map((p, index) => (
              <div
                key={p.id}
                className="inline-flex items-center space-x-2 bg-slate-50 border border-slate-200/80 hover:border-slate-300 rounded-xl pl-3 pr-2 py-1.5 shadow-sm transition"
              >
                <div className="w-6 h-6 rounded-full bg-brand/20 text-brand-hover text-xs font-bold flex items-center justify-center">
                  {p.name.charAt(0).toUpperCase()}
                </div>
                <span className="text-sm font-semibold text-navy-900">{p.name}</span>
                <button
                  type="button"
                  onClick={() => handleRemove(p.id)}
                  className="text-slate-400 hover:text-rose-500 p-0.5 rounded-md hover:bg-rose-50 transition"
                  title={`Remove ${p.name}`}
                >
                  <X className="w-3.5 h-3.5" />
                </button>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
